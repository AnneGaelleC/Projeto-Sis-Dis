package cryptographia;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class Chave {
	public static final String ALGORITHM = "RSA";
    public static final String PATH_CHAVE_PRIVADA = "C:/keys/private.key";
    
    public static final String PATH_CHAVE_PUBLICA = "C:/keys/public.key";
   
	public static void geraChave() {
	      try {
	        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
	        keyGen.initialize(1024);
	        final KeyPair key = keyGen.generateKeyPair();
	   
	        File chavePrivadaFile = new File(PATH_CHAVE_PRIVADA);
	        File chavePublicaFile = new File(PATH_CHAVE_PUBLICA);
	   
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
	   
}
