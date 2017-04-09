package cryptographia;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
 
 
public class Chave {
 
		PrivateKey privateKey;
		PublicKey publicKey;
		KeyPairGenerator keyGen;
	public PrivateKey getPrivateKey() {
			return privateKey;
		}

	public PublicKey getPublicKey() {
		return publicKey;
	}
	
	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}


  public static final String ALGORITHM = "RSA";
 
 
  /**
   * Gera a chave que contém um par de chave Privada e Pública usando 1025 bytes.
   * Armazena o conjunto de chaves nos arquivos private.key e public.key
   */
  public Chave(){
	 
  }
  
  public void geraChave(String path_public_key, String path_private_key) {
    try {
    SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
      keyGen = KeyPairGenerator.getInstance(ALGORITHM);
      keyGen.initialize(1024, random);
      final KeyPair key = keyGen.generateKeyPair();
 
     /* privateKey = key.getPrivate();
      publicKey = key.getPublic();*/
      
      File chavePrivadaFile = new File(path_private_key);
      File chavePublicaFile = new File(path_public_key);
 
      // Cria os arquivos para armazenar a chave Privada e a chave Publica
      if (chavePrivadaFile.getParentFile() != null) {
        chavePrivadaFile.getParentFile().mkdirs();
      }
      
      chavePrivadaFile.createNewFile();
 
      if (chavePublicaFile.getParentFile() != null) {
        chavePublicaFile.getParentFile().mkdirs();
      }
      
      chavePublicaFile.createNewFile();
 
      
      
      // Salva a Chave Pública no arquivo
      ObjectOutputStream chavePublicaOS = new ObjectOutputStream(
          new FileOutputStream(chavePublicaFile));
      chavePublicaOS.writeObject(key.getPublic());
      chavePublicaOS.close();
 
      // Salva a Chave Privada no arquivo
      ObjectOutputStream chavePrivadaOS = new ObjectOutputStream(
          new FileOutputStream(chavePrivadaFile));
      chavePrivadaOS.writeObject(key.getPrivate());
      chavePrivadaOS.close();
      
      
      
    } catch (Exception e) {
      e.printStackTrace();
    }
 
  }
 
  /**
   * Verifica se o par de chaves Pública e Privada já foram geradas.
   */
  public boolean verificaSeExisteChavesNoSO(String path_public_key, String path_private_key) {
 
    File chavePrivada = new File(path_private_key);
    File chavePublica = new File(path_public_key);
 
    if (chavePrivada.exists() && chavePublica.exists()) {
      return true;
    }
    
    return false;
  }
 
  /**
   * Criptografa o texto puro usando chave pública.
   */
  public byte[] criptografa(String texto, PublicKey chave) {
    byte[] cipherText = null;
    
    try {
      final Cipher cipher = Cipher.getInstance(ALGORITHM);
      // Criptografa o texto puro usando a chave Púlica
      cipher.init(Cipher.ENCRYPT_MODE, chave);
      cipherText = cipher.doFinal(texto.getBytes());
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return cipherText;
  }
  
  
  public byte[] sign(String texto, PrivateKey chave) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
	    Signature signer;
	    signer = Signature.getInstance("SHA1withRSA");
	    signer.initSign(chave); // PKCS#8 is preferred
	    signer.update(texto.getBytes());
	    byte[] signature = signer.sign();
	    return signature;
	  }
  
 
  /**
   * Decriptografa o texto puro usando chave privada.
   */
  public String decriptografa(byte[] texto, PrivateKey chave) {
    byte[] dectyptedText = null;
    
    try {
      final Cipher cipher = Cipher.getInstance(ALGORITHM);
      // Decriptografa o texto puro usando a chave Privada
      cipher.init(Cipher.DECRYPT_MODE, chave);
      dectyptedText = cipher.doFinal(texto);
 
    } catch (Exception ex) {
      ex.printStackTrace();
    }
 
    return new String(dectyptedText);
  }
 
  
  public boolean signatureCheck(byte[] data, PublicKey chave, byte[] sig) throws Exception{
	   
	  	Signature signer;
	  	signer = Signature.getInstance("SHA1withRSA");
		signer.initVerify(chave);
	    signer.update(data);
		
	  	boolean test = signer.verify(sig);
	  	System.out.println("");
	  	System.out.println("");
	  	System.out.println("");
	  	System.out.println("");
	  	System.out.println("");
	    return test;
	    //return new String(dectyptedText);
	  }
  /**
   * Testa o Algoritmo
   */
  /*public static void main(String[] args) {
 
    try {
 
      // Verifica se já existe um par de chaves, caso contrário gera-se as chaves..
      if (!verificaSeExisteChavesNoSO()) {
       // Método responsável por gerar um par de chaves usando o algoritmo RSA e
       // armazena as chaves nos seus respectivos arquivos.
        this.geraChave();
      }
 
      final String msgOriginal = "Exemplo de mensagem";
      ObjectInputStream inputStream = null;
 
      // Criptografa a Mensagem usando a Chave Pública
      inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PUBLICA));
      final PublicKey chavePublica = (PublicKey) inputStream.readObject();
      final byte[] textoCriptografado = criptografa(msgOriginal, chavePublica);
 
      // Decriptografa a Mensagem usando a Chave Pirvada
      inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PRIVADA));
      final PrivateKey chavePrivada = (PrivateKey) inputStream.readObject();
      final String textoPuro = decriptografa(textoCriptografado, chavePrivada);
 
      // Imprime o texto original, o texto criptografado e 
      // o texto descriptografado.
      System.out.println("Mensagem Original: " + msgOriginal);
      System.out.println("Mensagem Criptografada: " +textoCriptografado.toString());
      System.out.println("Mensagem Decriptografada: " + textoPuro);
 
    } catch (Exception e) {
      e.printStackTrace();
    }
  }*/
}