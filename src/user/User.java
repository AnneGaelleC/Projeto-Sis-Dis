package user;

import java.math.BigInteger;
import java.security.*;
import javax.crypto.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class User {
	private String name;
	private long code;
	private BigInteger publicKey ;
	private BigInteger privateKey;
	
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
	public void setpublicKey(BigInteger publicKey){
		this.publicKey=publicKey;
	}
	private void setprivatecKey(BigInteger privateKey){
		this.publicKey=privateKey;
	}
	public String getName(){
		return name;
	}
	public long getCode(){
		return code;
	}
	public BigInteger getPublicKey(){
		return publicKey;
	}
	private BigInteger getPrivateKey(){
		return privateKey;
	}
	
	public void initialization(){
		
		JFrame frame = new JFrame("User");
	    // prompt the user to enter his name
	    
	    while(this.name.isEmpty() == true){
	    	this.name = JOptionPane.showInputDialog(frame, "What's your name?", "Name", JOptionPane.QUESTION_MESSAGE);
	    	
	    	if(this.name == null){
	    		System.exit(0);
	    	}
	    	
	    	this.code=(long)name.hashCode();
	    	if(code<0){
	    		code = code*(-1);
	    	}
	    		
	    	System.out.println(name);
	    	System.out.println(code);
	    }
	}	
}
