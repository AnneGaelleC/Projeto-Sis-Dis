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
	ArrayList< Product > AvailableProductsList= new ArrayList< Product >();
	ArrayList< Product > WantedProductsList = new ArrayList< Product >();


	public Buyer(User u){
		name = u.getName();
		code = u.getCode();
		myClientIp = u.getMyClientIp();
		tcpClient = new TCPClient();
		connectionManager = u.connectioManager;
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
		
		AvailableProductsList = connectionManager.getProductsList();
	}
	
	public ArrayList<Product> getProductsList() {
		return AvailableProductsList;
	}
	
	public void bide(int productCode, int selleCode, float bide, Product product)
	{
		//tcpClient.Connect();
	}
	public void startCommunicationTCP(int newBide){
		
		setBide(newBide);
	}
	
	/**
	 * 
	 * @param p code of a product
	 * @return true if the Product p is already on the buyer's list
	 */
	public boolean checkProducts(int p, int s)
	{
		WantedProductsList.contains(p);
		
		for(int i = 0; i< WantedProductsList.size(); i++)
		{
			if(WantedProductsList.get(i).getProductCode() == p && WantedProductsList.get(i).getSellerCode() == s)
			{
				return true;
			}
		}
		
		return false;
	}
		
}
