package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import auction.Product;
import user.User;

public class MultiCast {
  private MulticastSocket s;
  private InetAddress group;
  private byte[] buffer;	
  private MulticastListener multicastListener; //thread to listen all messages in this multicast group
  private int port;
  /**
   * Constructor
   */
  public MultiCast(){
	  s = null;
	  group = null;
	  buffer = new byte[1000];
	  multicastListener = new MulticastListener();
  }
  
  /**
   * 
   * @param ip the ip of the multicast group
   * @param port the port to connect this process
   * @throws IOException
   */
  public void connect(String ip, int port, String myIp) throws IOException {
        // Could be used DatagramSocket instead if the server only sends message and doesn't receive other peers message.
        try {
        	this.port = port;
        	s = new MulticastSocket(port);
        	group = InetAddress.getByName(ip);
			s.joinGroup(group);
			multicastListener.setMulticastSocket(s);
			multicastListener.setMyIp(myIp);
			multicastListener.start();
        
  		} catch (SocketException e) {
  			System.out.println("Socket: " + e.getMessage());
  		}
    }
  
  /**
   * 
   * @param output to send by multicast
   * @throws IOException
   */
	public void send(byte[] output) throws IOException{
		 DatagramPacket indp = new DatagramPacket(output, output.length, group, port);
	  
		 if(s!= null)
			 s.send(indp);
	  
		 if ("Stop".equalsIgnoreCase(output.toString()))
		 {	            
			 s.leaveGroup(group);
		 }
	}

  	public ArrayList<Product> getProductsList() {
		return multicastListener.getProductsList();
	}
  
	public MulticastSocket getS() {
		return s;
	}
	
	public void setS(MulticastSocket s) {
		this.s = s;
	}
	
	public InetAddress getGroup() {
		return group;
	}
	
	public void setGroup(InetAddress group) {
		this.group = group;
	}
	
	public byte[] getBuffer() {
		return buffer;
	}
	
	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}
	
	public MulticastListener getMulticastListener() {
		return multicastListener;
	}
	
	public void setMulticastListener(MulticastListener multicastListener) {
		this.multicastListener = multicastListener;
	}
	
	public ArrayList<User> getUsersList()
	{
		return multicastListener.getUserList();
		
	}
}


