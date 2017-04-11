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
	
	public void sendBid(int myCode, int productCode, float bidValue, byte[] check, String sellerIp, int sellerPort) throws ClassNotFoundException, IOException
	{
		boolean sended =false;
		for(int i=0; i< udpClientsList.size(); i++)
		{
			if(udpClientsList.get(i).getIp() == sellerIp && udpClientsList.get(i).getPort()== sellerPort)
			{
				System.out.println("(ConnectionManager)enviando bid...");
	           /* ByteArrayOutputStream bos = new ByteArrayOutputStream(10);
	            ObjectOutputStream oos;*/
	            
	            /*ByteArrayOutputStream baos=new ByteArrayOutputStream(10000);
	            DataOutputStream daos=new DataOutputStream(baos);*/
	            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
	            ObjectOutputStream oos = new ObjectOutputStream(bos);
				try {
					oos.writeChar('B');
					oos.writeInt(productCode);
					oos.writeInt(myCode);
					oos.writeFloat(bidValue);
					oos.writeObject(check);
					/*byte[] data=str.getBytes("UTF-8");
					out.writeInt(data.length);
					out.write(data);

					// Read data
					int length=in.readInt();
					byte[] data=new byte[length];
					in.readFully(data);*/
					
					//System.out.println("conectionManager: "+check.length);
					oos.flush();
    	            byte[] output = bos.toByteArray();
    	            udpClientsList.get(i).SenMessage(output);
    	            sended = true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
			}
		}
		
		if(sended ==false)
		{
			createNewClietForMe(sellerIp, sellerPort);
			System.out.println("(ConnectionManager)enviando bid...");
            /*ByteArrayOutputStream bos = new ByteArrayOutputStream(10);
            ObjectOutputStream oos;*/
			/*ByteArrayOutputStream baos=new ByteArrayOutputStream(10000);
            DataOutputStream daos=new DataOutputStream(baos);*/
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            ObjectOutputStream oos = new ObjectOutputStream(bos);
			try {
				
				oos.writeChar('B');
				oos.writeInt(productCode);
				oos.writeInt(myCode);
				oos.writeFloat(bidValue);
				oos.writeObject(check);
				
				//System.out.println(check.length);
				oos.flush();
	            byte[] output = bos.toByteArray();
	            udpClientsList.get(udpClientsList.size()-1).SenMessage(output);
	            sended = true;
	            System.out.println("conectionManager: "+check.length);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
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
	
	public void sendPriceUpdate(String ServerIp, int clientPort,  int productCode, int sellerCode, float newValue)
	{
		boolean sended =false;
		for(int i=0; i< udpClientsList.size(); i++)
		{
			if(udpClientsList.get(i).getIp() == ServerIp && udpClientsList.get(i).getPort()== clientPort)
			{
	            ByteArrayOutputStream baos=new ByteArrayOutputStream(1000);
	            DataOutputStream daos=new DataOutputStream(baos);
	            
				try {
					
					daos.writeChar('U');
					daos.writeInt(productCode);
					daos.writeInt(sellerCode);
					daos.writeFloat(newValue);
					
					/*byte[] data=str.getBytes("UTF-8");
					out.writeInt(data.length);
					out.write(data);

					// Read data
					int length=in.readInt();
					byte[] data=new byte[length];
					in.readFully(data);*/
					
					//System.out.println(check.length);
					daos.flush();
    	            byte[] output = baos.toByteArray();
    	            udpClientsList.get(i).SenMessage(output);
    	            sended = true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
			}
		}
		
		if(sended ==false)
		{
			createNewClietForMe(ServerIp, clientPort);
			
			ByteArrayOutputStream baos=new ByteArrayOutputStream(1000);
            DataOutputStream daos=new DataOutputStream(baos);
			try {
				
				daos.writeInt(1);
				daos.writeInt(productCode);
				daos.writeInt(sellerCode);
				daos.writeFloat(newValue);
				
				//System.out.println(check.length);
				daos.flush();
	            byte[] output = baos.toByteArray();
	            udpClientsList.get(udpClientsList.size()-1).SenMessage(output);
	            sended = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
}//end class
