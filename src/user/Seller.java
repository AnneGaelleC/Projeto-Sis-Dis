package user;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import auction.Product;
import communication.*;


public class Seller extends User{
	
	private TCPClient tcpClient; 
	ConnectionManager connectionManager;
	ArrayList< Product > productsList = new ArrayList< Product >();
	private int port;
	private TCPClient TCPClient; 
	
	public Seller(){
		
	}
	
	public Seller(User u){
		name = u.getName();
		code = u.getCode();
		myClientIp = u.getMyClientIp(); 
		connectionManager = u.connectioManager;
		
		
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
        oos.writeObject(this.getMyClientIp());
        oos.flush();
        byte[] output = bos.toByteArray();
        
        connectionManager.sendMulticastMessage(output);
        
		
	}

}
