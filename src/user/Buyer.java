package user;
import java.util.ArrayList;

import auction.Product;
import communication.*;

public class Buyer extends User {
    private int Bide;
    private TCPServer TCPServer; 
	ConnectionManager connectionManager;
	private TCPServer tCPServer; 
	private TCPClient tcpClient;
	ArrayList< Product > productsList = new ArrayList< Product >();


	public Buyer(User u){
		name = u.getName();
		code = u.getCode();
		myIp = u.getMyIp();
		connectionManager = u.getconnectionManager();
		tcpClient = new TCPClient();
	}
	
	


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
		//tcpClient.Connect();
	}
	public void startCommunicationTCP(int newBide){
		
		setBide(newBide);
	}
		
}
