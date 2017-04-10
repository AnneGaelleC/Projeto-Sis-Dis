package communication;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Enumeration;
import auction.Product;
import user.User;
import java.util.Random;


public class ConnectionManager {
	private MultiCast multicastConnection;
	private String myIp;
	private String multicastIp;
	private int multicastPort;
	private int tcpServerPort;
	private int tcpClientPort;
	TCPServer tcpServer;
	ArrayList< TCPClient > tcpClientsList = new ArrayList< TCPClient >();
	
	public ConnectionManager(){
		Random randomGenerator = new Random();
		randomGenerator.setSeed(System.currentTimeMillis());
		myIp = "127.0.0.1";
		multicastPort = 1234;
		multicastIp = "228.5.6.7";
		multicastConnection = new MultiCast();
		tcpServerPort = randomGenerator.nextInt(10000)+1234;
		//tcpClientPort = randomGenerator.nextInt(10000)+1234;
		tcpServer = new TCPServer();
	}
	
	public int getTcpServerPort()
	{
		return tcpServerPort;
	}
	/**
	 * this method discover the local local Ip of an interface if it is 192. or 10.
	 * @throws UnknownHostException
	 */
	private void discoverIp(){
		Enumeration e;
		try {
			e = NetworkInterface.getNetworkInterfaces();
			while(e.hasMoreElements()) //Get network interfaces on PC
			{
			    NetworkInterface n = (NetworkInterface) e.nextElement();
			    Enumeration ee = n.getInetAddresses();
			    while (ee.hasMoreElements())//get informations on this interface IPv4 and IPv6
			    {
			        InetAddress i = (InetAddress) ee.nextElement();
			        if(i.getHostAddress().toString().contains("192.") || i.getHostAddress().toString().contains("10."))
			        {
			        	myIp = i.getHostAddress().toString();
			        	//System.out.println(i.getHostAddress());
			        } 
			    }
			}
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void initConnections() throws UnknownHostException{
		discoverIp();
		try {
			//try start a multicast connection
			multicastConnection.connect(multicastIp, multicastPort, multicastIp);
			tcpServer.setServerPort(tcpServerPort);
			tcpServer.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getMulticastIp() {
		// TODO Auto-generated method stub
		return multicastIp;
	}

	public void sendMulticastMessage(byte[] output) {
		try {
			multicastConnection.send(output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getIp() {
		// TODO Auto-generated method stub
		return myIp;
	}
	
	public ArrayList<Product> getProductsList() {
		return multicastConnection.getProductsList();
	}
	
	public ArrayList<User> getUsersList()
	{
		return multicastConnection.getUsersList();
	}
	
	public void sendBid(int sellerCode, int productCode, float bidValue, byte[] check, String sellerIp, int sellerPort)
	{
		for(int i=0; i< tcpClientsList.size(); i++)
		{
			if(tcpClientsList.get(i).getMyIp() == sellerIp)
			{
	            ByteArrayOutputStream bos = new ByteArrayOutputStream(10);
	            ObjectOutputStream oos;
				try {
					oos = new ObjectOutputStream(bos);
					oos.writeInt(productCode);
		            oos.writeFloat(bidValue);
		            oos.writeObject(check);
		            oos.flush();
    	            byte[] output = bos.toByteArray();
    	            tcpClientsList.get(i).SenMessage(output.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
			}
		}
	}
	
	public void createNewClietForMe(String sellerIp, int sellerPort)
	{
		for(int i=0; i< tcpClientsList.size(); i++)
		{
			if(tcpClientsList.get(i).getMyIp() != sellerIp/* && sellerIp != myIp*/)
			{
				TCPClient tcpClient = new TCPClient();
				tcpClient.Connect (sellerPort, sellerIp);
				tcpClientsList.add(tcpClient);
				System.out.println("new client created");
			}
		}	
	}
}//end class
