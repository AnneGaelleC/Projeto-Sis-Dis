package communication;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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
	UDPServer tcpServer;
	ArrayList< UDPClient > udpClientsList = new ArrayList< UDPClient >();
	
	public ConnectionManager(){
		Random randomGenerator = new Random();
		randomGenerator.setSeed(System.currentTimeMillis());
		myIp = "127.0.0.1";
		multicastPort = 1234;
		multicastIp = "228.5.6.7";
		multicastConnection = new MultiCast();
		tcpServerPort = randomGenerator.nextInt(5000)+1234;
		//tcpClientPort = randomGenerator.nextInt(10000)+1234;
		tcpServer = new UDPServer();
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
			multicastConnection.connect(multicastIp, multicastPort, myIp);
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
	
	
	
	public void sendBid(String sellerIp, int sellerPort, byte[] message) 
	{
		boolean sended =false;
		for(int i=0; i< udpClientsList.size(); i++)
		{
			if(udpClientsList.get(i).getIp() == sellerIp && udpClientsList.get(i).getPort()== sellerPort)
			{
				System.out.println("(ConnectionManager)enviando bid...");
				udpClientsList.get(i).SenMessage(message);
				sended = true;  
			}
		}
		
		if(sended ==false)
		{
			createNewClietForMe(sellerIp, sellerPort);
			System.out.println("(ConnectionManager)enviando bid...");
			
			for(int i=0; i< udpClientsList.size(); i++)
			{
				if(udpClientsList.get(i).getIp() == sellerIp && udpClientsList.get(i).getPort()== sellerPort)
				{
					udpClientsList.get(i).SenMessage(message);
				}
			}
	        //udpClientsList.get(udpClientsList.size()-1).SenMessage(message);
	        sended = true;
		}
	}
	
	
	
	
	public void createNewClietForMe(String sellerIp, int sellerPort)
	{
		boolean create = true;
		for(int i=0; i< udpClientsList.size(); i++)
		{
			if(udpClientsList.get(i).getIp() == sellerIp && udpClientsList.get(i).getPort() == sellerPort)
			{
				create = false;
				break;
			}
		}
		
		if(create == true)
		{
			UDPClient tcpClient = new UDPClient();
			tcpClient.setIp(sellerIp);
			tcpClient.setPort(sellerPort);
			tcpClient.Connect (sellerPort, sellerIp);
			udpClientsList.add(tcpClient);
			System.out.println("new client created");
		}
	}
	
	public void sendPriceUpdate(String ServerIp, int clientPort,  byte[] update)
	{
		
		boolean sended =false;
		for(int i=0; i< udpClientsList.size(); i++)
		{
			if(udpClientsList.get(i).getIp() == ServerIp && udpClientsList.get(i).getPort()== clientPort)
			{
				System.out.println("(ConnectionManager)enviando bid...");
				udpClientsList.get(i).SenMessage(update);
				sended = true;  
			}
		}
		
		if(sended ==false)
		{
			createNewClietForMe(ServerIp, clientPort);
			System.out.println("(ConnectionManager)enviando bid...");
			
			for(int i=0; i< udpClientsList.size(); i++)
			{
				if(udpClientsList.get(i).getIp() == ServerIp && udpClientsList.get(i).getPort()== clientPort)
				{
					udpClientsList.get(i).SenMessage(update);
				}
			}
	        //udpClientsList.get(udpClientsList.size()-1).SenMessage(message);
	        sended = true;
		}
	}
	
	
	public boolean sendIsAliveMessage(String sellerIp, int sellerPort, byte[] message) 
	{
		boolean replayOk = false;
		boolean sended =false;
		for(int i=0; i< udpClientsList.size(); i++)
		{
			if(udpClientsList.get(i).getIp() == sellerIp && udpClientsList.get(i).getPort()== sellerPort)
			{
				System.out.println("(ConnectionManager)asking if server is alive...");
				replayOk = udpClientsList.get(i).SendMessageAlive(message);
				sended = true;  
			}
		}
		
		if(sended ==false)
		{
			createNewClietForMe(sellerIp, sellerPort);
			System.out.println("(ConnectionManager)asking if server is alive...");
			
			for(int i=0; i< udpClientsList.size(); i++)
			{
				if(udpClientsList.get(i).getIp() == sellerIp && udpClientsList.get(i).getPort()== sellerPort)
				{
					replayOk = udpClientsList.get(i).SendMessageAlive(message);
				}
			}
	        //udpClientsList.get(udpClientsList.size()-1).SenMessage(message);
	        sended = true;
		}
		return replayOk;
	}
	
}//end class
