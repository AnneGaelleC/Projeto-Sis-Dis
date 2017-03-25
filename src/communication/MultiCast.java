package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MultiCast {
	  MulticastSocket s = null;
	  InetAddress group = null;
	  byte[] buffer = new byte[1000];	  
	  public void connect(String ip, int port) throws IOException {
	        // Could be used DatagramSocket instead if the server only sends message and doesn't receive other peers message.
	        try {
	        	group =  InetAddress.getByName(ip);
				s.joinGroup(group);
				s= new MulticastSocket(port);
	        
	  		} catch (SocketException e) {
          System.out.println("Socket: " + e.getMessage());}
	  		}

          public void send(String message) throws IOException{
	                byte [] messagetoSend = message.getBytes();
	                DatagramPacket indp = new DatagramPacket(messagetoSend, messagetoSend.length);
	                s.send(indp);
	                if ("Stop".equalsIgnoreCase(message)){	            
	                	s.leaveGroup(group);
	            }
	    }
}


