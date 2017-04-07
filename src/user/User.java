package user;
import cryptographia.*;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
//import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import communication.ConnectionManager;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class User {
	protected String name;
	protected int code;
	protected String myClientIp;
	private Chave cryptKey;
	protected ConnectionManager connectioManager;
	
	public String getMyClientIp() {
		return myClientIp;
	}
	
	public void setMyClientIp(String myClientIp) {
		this.myClientIp = myClientIp;
	}

	public User(){
		name = "";
		code = 0;
	}
	
	public ConnectionManager getConnectioManager() {
		return connectioManager;
	}
	
	public void setConnectioManager(ConnectionManager connectioManager) {
		this.connectioManager = connectioManager;
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
	    	if(code<0){
	    		code = code*(-1);
	    	}
	    	//generate the public and private Key for criptographia
	    	if (!cryptKey.verificaSeExisteChavesNoSO())
	    	{
	    		cryptKey.geraChave();
	    	}
	    	
	    	connectioManager = new ConnectionManager();
			try {
				connectioManager.initConnections();
				myClientIp = connectioManager.getIp();
				ObjectInputStream inputStream = null;
	    		String PATH_CHAVE_PUBLICA = "./Key/public.key";
	    		inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PUBLICA));
	    		PublicKey publicKey = (PublicKey) inputStream.readObject();
	            ByteArrayOutputStream bos = new ByteArrayOutputStream(10);
	            ObjectOutputStream oos = new ObjectOutputStream(bos);
	            oos.writeChar('H');
	            oos.writeObject(name);
	            oos.writeInt(code);
	            oos.writeObject(connectioManager.getIp());
	            oos.writeObject(publicKey);
	            oos.flush();
	            byte[] output = bos.toByteArray();
				
				//Send the hello message when the user enter the multicast group
				connectioManager.sendMulticastMessage(output);
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }	    
	}
}//end class