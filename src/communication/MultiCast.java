package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MultiCast {
  private MulticastSocket s;
  private InetAddress group;
  private byte[] buffer;	
  private MulticastListener multicastListener; //thread to listen all messages in this multicast group
  
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
  public void connect(String ip, int port) throws IOException {
        // Could be used DatagramSocket instead if the server only sends message and doesn't receive other peers message.
        try {
        	s = new MulticastSocket(port);
        	group = InetAddress.getByName(ip);
			s.joinGroup(group);
			s= new MulticastSocket(port);
			multicastListener.setMulticastSocket(s);
			multicastListener.start();
        
  		} catch (SocketException e) {
  			System.out.println("Socket: " + e.getMessage());
  		}
    }
  
  /**
   * 
   * @param message to send by multicast
   * @throws IOException
   */
  public void send(String message) throws IOException{
      byte [] messagetoSend = message.getBytes();
      DatagramPacket indp = new DatagramPacket(messagetoSend, messagetoSend.length);
      s.send(indp);
      if ("Stop".equalsIgnoreCase(message)){	            
      	s.leaveGroup(group);
      }
  }
}


