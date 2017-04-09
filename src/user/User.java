package user;
import cryptographia.*;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
//import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
//import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
//import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
//import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
//import java.util.Base64;
//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;
import java.util.Timer;
import java.util.TimerTask;

import communication.ConnectionManager;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import auction.Product;

public class User {
	protected String name;
	protected int code;
	protected String myClientIp;
	private Chave cryptKey;
	protected ConnectionManager connectionManager;
	private int myServerPort;
	
	

	ArrayList< Product > productsIamSellingList = new ArrayList< Product >();
	ArrayList< Product > AvailableProductsList= new ArrayList< Product >();
	ArrayList< Product > WantedProductsList = new ArrayList< Product >();
	ArrayList< User > othersUsersList = new ArrayList< User >();
	String PATH_CHAVE_PUBLICA;
	String PATH_CHAVE_PRIVADA;
	
	Timer timerCheckNewUser;
	
	public int getMyServerPort() {
		return myServerPort;
	}

	public void setMyServerPort(int myServerPort) {
		this.myServerPort = myServerPort;
	}
	
	public static long getPID() {
	    String processName =
	      java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
	    return Long.parseLong(processName.split("@")[0]);
	  }
	
	public String getMyClientIp() {
		return myClientIp;
	}
	
	public void setMyClientIp(String myClientIp) {
		this.myClientIp = myClientIp;
	}

	public User(){
		name = "";
		code = 0;
		cryptKey = new Chave();
	}
	
	public ConnectionManager getconnectionManager() {
		return connectionManager;
	}
	
	public void setconnectionManager(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}
	
	public void setName(String name){
		this.name=name;
	}
	
	public void setCode(int code){
		this.code=code;
	}
	
	public String getName(){
		return name;
	}
	
	public int getCode(){
		return code;
	}
	
	public void initialization() throws ClassNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException{
		
		JFrame frame = new JFrame("User");
	    // prompt the user to enter his name
	    
	    while(this.name.isEmpty() == true){
	    	this.name = JOptionPane.showInputDialog(frame, "What's your name?", "Name", JOptionPane.QUESTION_MESSAGE);
	    	
	    	if(this.name == null){
	    		System.exit(0);
	    	}
	    	
	    	this.code=(int)name.hashCode();
	    	this.code = (int) (this.code + getPID());
	    	if(code<0){
	    		code = code*(-1);
	    	}
	    	this.code = this.code%10000; //this let the code with 4 digits
	    	
	    	PATH_CHAVE_PUBLICA = "./Key/"+ Integer.toString(this.code)+"public.key";
	    	PATH_CHAVE_PRIVADA = "./Key/"+ Integer.toString(this.code)+"private.key";
	    	//generate the public and private Key for criptographia
	    	if (!cryptKey.verificaSeExisteChavesNoSO(PATH_CHAVE_PUBLICA, PATH_CHAVE_PRIVADA))
	    	{
	    		cryptKey.geraChave(PATH_CHAVE_PUBLICA, PATH_CHAVE_PRIVADA);
	    	}
	    	
	    	connectionManager = new ConnectionManager();
			try {
				connectionManager.initConnections();
				myClientIp = connectionManager.getIp();
				myServerPort = connectionManager.getTcpServerPort();
				ObjectInputStream inputStream = null;
	    		inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PUBLICA));
	    		PublicKey publicKey = (PublicKey) inputStream.readObject();
	            ByteArrayOutputStream bos = new ByteArrayOutputStream(10);
	            ObjectOutputStream oos = new ObjectOutputStream(bos);
	            oos.writeChar('H');
	            oos.writeObject(name);
	            oos.writeInt(code);
	            oos.writeObject(connectionManager.getIp());
	            oos.writeInt(myServerPort);
	            oos.writeObject(publicKey);
	            oos.flush();
	            byte[] output = bos.toByteArray();
				
				//Send the hello message when the user enter the multicast group
				connectionManager.sendMulticastMessage(output);
				inputStream.close();
				
				this.chekNewUsers(10);
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }	    
	}
	
	public void SellNewProduct(Product newProduct) throws IOException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, SignatureException
	{
		ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PRIVADA));
		PrivateKey pk = (PrivateKey) inputStream.readObject();
		//PrivateKey pk = cryptKey.getPrivateKey();
		//System.out.println(publicKey.toString());
		byte [] encrypted = cryptKey.sign(this.name, pk);
		//String encrypted = "asd";
		newProduct.setAuthenticityCheck(encrypted.toString());
		productsIamSellingList.add(newProduct);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream(10);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeChar('N');
        oos.writeObject(name); //seller name
        oos.writeInt(code); //seller code
        oos.writeObject(newProduct.getProductName());
        oos.writeInt(newProduct.getProductCode());
        oos.writeObject(newProduct.getDescription());
        oos.writeFloat(newProduct.getInitialPrice());
        oos.writeInt(newProduct.getEndTime());
        oos.writeObject(this.getMyClientIp());
        oos.writeObject(encrypted);
        oos.flush();
        byte[] output = bos.toByteArray();
        
        connectionManager.sendMulticastMessage(output);
        
		
	}
	
	public void updateProductsList()
	{
		
		AvailableProductsList = connectionManager.getProductsList();
	}
	
	public ArrayList<Product> getProductsList() {
		return AvailableProductsList;
	}
	
	public void setPublicKey(PublicKey pk)
	{
		cryptKey.setPublicKey(pk);
	}
	
	public PublicKey getPublicKey()
	{
		return cryptKey.getPublicKey();
	}
	
	
	
	private void chekNewUsers(int seconds) {
		timerCheckNewUser = new Timer();
		//timerCheckNewUser.schedule(new sendMessagesToNewUsers(), seconds*1000);
		timerCheckNewUser.scheduleAtFixedRate(new sendMessagesToNewUsers(), 0, seconds*1000);
    }

    class sendMessagesToNewUsers extends TimerTask {
        public void run() {
            System.out.println("new Users check...");
            ArrayList< User > userList = new ArrayList< User >();
            userList = connectionManager.getUsersList();
            //timerCheckNewUser.cancel(); //Terminate the timer thread
            
            if(userList.size() != othersUsersList.size())
            {
            	System.out.println("New Users detected: sending hello message...");
            	//I send my hello again
            	try {
    				ObjectInputStream inputStream = null;
    	    		inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PUBLICA));
    	    		PublicKey publicKey = (PublicKey) inputStream.readObject();
    	            ByteArrayOutputStream bos = new ByteArrayOutputStream(10);
    	            ObjectOutputStream oos = new ObjectOutputStream(bos);
    	            oos.writeChar('H');
    	            oos.writeObject(name);
    	            oos.writeInt(code);
    	            oos.writeObject(connectionManager.getIp());
    	            oos.writeInt(connectionManager.getTcpServerPort());
    	            oos.writeObject(publicKey);
    	            oos.flush();
    	            byte[] output = bos.toByteArray();
    				
    				//Send the hello message when the user enter the multicast group
    				connectionManager.sendMulticastMessage(output);
    				inputStream.close();
    				othersUsersList = userList;
    				
    			} catch (UnknownHostException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
            //chekNewUsers(10);
        }
    }
    
    public boolean checkAuthenticity(String userName, PublicKey pk, byte[] signature) throws Exception
    {
    		return cryptKey.signatureCheck(userName.getBytes(), pk, signature);
    }
}//end class