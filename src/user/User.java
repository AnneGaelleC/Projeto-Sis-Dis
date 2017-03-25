package user;

import java.math.BigInteger;
import java.security.*;
import javax.crypto.*;
import javax.swing.JOptionPane;

public class User {
	private String name;
	private int code;
	private BigInteger publicKey ;
	private BigInteger privateKey;
	
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
	public int getCode(){
		return code;
	}
	public BigInteger getPublicKey(){
		return publicKey;
	}
	private BigInteger getPrivateKey(){
		return privateKey;
	}
	
	public void initialization(){
		final JOptionPane optionPane= new JOptionPane("empty",
		JOptionPane.QUESTION_MESSAGE);
	}
	
}
