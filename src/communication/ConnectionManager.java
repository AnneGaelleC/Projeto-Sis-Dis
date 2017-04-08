package communication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

import auction.Product;
import user.User;

public class ConnectionManager {
	private MultiCast multicastConnection;
	private String ip;
	private String multicastIp;
	private static int port;
	
	public ConnectionManager(){
		ip = "127.0.0.1";
		port = 1234;
		multicastIp = "228.5.6.7";
		multicastConnection = new MultiCast();
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
			        	ip = i.getHostAddress().toString();
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
			multicastConnection.connect(multicastIp, port, ip);
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
		return ip;
	}
	
	public ArrayList<Product> getProductsList() {
		return multicastConnection.getProductsList();
	}
	
	public ArrayList<User> getUsersList()
	{
		return multicastConnection.getUsersList();
	}
}//end class
