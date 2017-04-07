package user;
<<<<<<< HEAD
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import auction.Product;
=======
>>>>>>> 5a676652b1b29bfbb994c85d55d50de4a922b748
import communication.*;

public class Seller extends User{
	
	private User Seller;
<<<<<<< HEAD
	private String name;
	private int code; 
	private TCPClient TCPClient; 
	ConnectionManager connectionManager;
	ArrayList< Product > productsList = new ArrayList< Product >();
	private int port;
	
	public Seller(User u){
		name = u.getName();
		code = u.getCode();
		myIp = u.getMyIp();
		connectionManager = u.getConnectioManager();
		
		
	}
	public Seller(){
		
	}
	
	public void SellNewProduct(Product newProduct) throws IOException
	{
		productsList.add(newProduct);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream(10);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeChar('N');
        oos.writeObject(name); //seller name
        oos.writeInt(code); //seller code
        oos.writeObject(newProduct.getProductName());
        oos.writeInt(newProduct.getProductCode());
        oos.writeObject(newProduct.getDescription());
        oos.writeFloat(newProduct.getPrice());
        oos.writeFloat(newProduct.getEndTime());
        oos.writeObject(this.getMyIp());
        oos.flush();
        byte[] output = bos.toByteArray();
        
        connectionManager.sendMulticastMessage(output);
        
		
	}
=======
	private String name = Seller.getName();
	private int code = Seller.getCode(); 
	private TCPClient TCPClient; 
	
	

	
	
	
>>>>>>> 5a676652b1b29bfbb994c85d55d50de4a922b748

}
