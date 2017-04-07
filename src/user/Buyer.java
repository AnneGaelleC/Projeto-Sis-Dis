package user;
import communication.*;

public class Buyer extends User {
    private int Bide;
	private User Buyer;
	private String name = Buyer.getName();
	private int code = Buyer.getCode(); 
	private TCPServer TCPServer; 
	private ConnectionServer ConnectionServer;


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
	
	public void startCommunicationTCP(int newBide){
		ConnectionServer = new ConnectionServer();
		ConnectionServer.initConnections();
		setBide(newBide);
		
	}
}
