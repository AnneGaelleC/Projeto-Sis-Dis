package user;
import cryptographia.*;

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
	private String name;
	private int code;
	private ConnectionManager connectioManager;
	private PublicKey publicKey ;
	private PrivateKey privateKey;
	private Chave cryptKey;
	private String HelloMulticastMessage;
	private String publicKeyString;
	
	public User(){
		name = "";
		code = 0;
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
	    	ObjectInputStream inputStream = null;
    		String PATH_CHAVE_PUBLICA = "./Key/public.key";
    		inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PUBLICA));
    		publicKey = (PublicKey) inputStream.readObject();
    		//String encodedKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
    		publicKeyString = publicKey.toString();
    		System.out.println(publicKeyString);
    		byte[] publicKeyBytes = publicKey.getEncoded();
    		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
    	    PublicKey publicKey2 = keyFactory.generatePublic(publicKeySpec);
    	    System.out.println(publicKey2.toString());
    		/*X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(clear);
    		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    		PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
    		System.out.println(pubKey.toString());*/
    		
    		
    		/*byte data[] = publicKeyString.getBytes("UTF-8");
    		String encoded = Base64.getEncoder().encodeToString(data);
    		byte[] decoded = Base64.getDecoder().decode(encoded);
    		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
    		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    		PublicKey pubKey = keyFactory.generatePublic(keySpec);
    		System.out.println(pubKey.toString());*/
    		
    		//byte[] publicBytes = Base64.getDecoder().decode(publicKeyString);
    		
    		/*byte data[] = publicKey.getEncoded();
    		
    		System.out.println(data.toString());*/
    		
	    	connectioManager = new ConnectionManager();
			try {
				connectioManager.initConnections();
				setHelloMulticastMessage(name+";"+Integer.toString(code)+";"+connectioManager.getMulticastIp()+";"+publicKeyString);
		    	
				//Send the hello message when the user enter the multicast group
				connectioManager.sendMulticastMessage(HelloMulticastMessage);
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	    	//DEBUG
	    	//System.out.println(name);
	    	//System.out.println(code);
	    }
	    
	}
	public String getHelloMulticastMessage() {
		return HelloMulticastMessage;
	}
	public void setHelloMulticastMessage(String helloMulticastMessage) {
		HelloMulticastMessage = helloMulticastMessage;
	}	
}