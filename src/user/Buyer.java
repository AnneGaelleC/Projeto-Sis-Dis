package user;
<<<<<<< HEAD
import java.util.ArrayList;

import auction.Product;
=======
>>>>>>> 5a676652b1b29bfbb994c85d55d50de4a922b748
import communication.*;

public class Buyer extends User {
    private int Bide;
	private User Buyer;
<<<<<<< HEAD
	private String name;
	private int code;
	ConnectionManager connectionManager;
	private TCPServer tCPServer; 
	private TCPClient tcpClient;
	ArrayList< Product > productsList = new ArrayList< Product >();


	public Buyer(User u){
		name = u.getName();
		code = u.getCode();
		myIp = u.getMyIp();
		connectionManager = u.getConnectioManager();
		tcpClient = new TCPClient();
	}
	
=======
	private String name = Buyer.getName();
	private int code = Buyer.getCode(); 
	private TCPServer TCPServer; 
	private ConnectionServer ConnectionServer;


>>>>>>> 5a676652b1b29bfbb994c85d55d50de4a922b748
	public int getBide() {
		return Bide;
	}

	public void setBide(int bide) {
		Bide = bide;
	}
	
	private void outBide(int newBide){
		if (newBide>Bide){
			Bide=newBide;
		}
		else{
			System.out.println("New bide can't be lower than the previous bide");
		}
	}
	
<<<<<<< HEAD
	public void startCommunicationTCP(int newBide, int port){
		tCPServer = new TCPServer();
		tCPServer.ServerListener(port);
		setBide(newBide);
		
	}
	
	public void updateProductsList()
	{
		
		productsList = connectionManager.getProductsList();
	}
	
	public ArrayList<Product> getProductsList() {
		return productsList;
	}
	
	public void bide(int productCode, int selleCode, float bide, Product product)
	{
		tcpClient.Connect()
	}
=======
	public void startCommunicationTCP(int newBide){
		ConnectionServer = new ConnectionServer();
		ConnectionServer.initConnections();
		setBide(newBide);
		
	}
>>>>>>> 5a676652b1b29bfbb994c85d55d50de4a922b748
}
