package communication;

import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import auction.Bid;
import user.User;

import java.io.*;
public class UDPServer extends Thread {
	int serverPort;
	public static ArrayList< Bid > requests = new ArrayList< Bid >();
	public static Semaphore semaphore = new Semaphore(1);
	
	/**
	 * constructur of this class
	 */
	public UDPServer(){
		serverPort = 1235;
	}
	/**
	 * Set a new port for this server
	 * @param port port number
	 */
	public void setServerPort(int port){
		serverPort = port;
	}
	
	public void run(){
		System.out.println("running server");
		try {
			ServerListener(serverPort);
		} catch (ClassNotFoundException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * SeverUdp keep listening his port waiting for new connections
	 * @param port : port number
	 * @throws ClassNotFoundException
	 * @throws InterruptedException
	 */
	public void ServerListener (int port) throws ClassNotFoundException, InterruptedException {
		DatagramSocket aSocket = null;
		try{
	    	aSocket = new DatagramSocket(port);
					// create socket at agreed port
			byte[] buffer = new byte[1000];
 			while(true){
 				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
  				aSocket.receive(request); 
  				/*ByteArrayInputStream bais=new ByteArrayInputStream(request.getData());
  		        DataInputStream dais=new DataInputStream(bais);*/
  		        
  				//ByteArrayInputStream bis = new ByteArrayInputStream(request.getData());
  			    //ObjectInputStream ois = new ObjectInputStream(bis);
  		      ByteArrayInputStream bis = new ByteArrayInputStream(request.getData());
  		      ObjectInputStream ois = new ObjectInputStream(bis);
  		         char type = ois.readChar();
  		         if(type == 'B')
  		         {
  		        	 byte [] checkB = new byte[128];
  		        	 int productCode = ois.readInt();
  				     int userCode= ois.readInt();
  				     float bidValue = ois.readFloat();
  				     //String check = dais.readUTF();
  				     checkB = (byte[])ois.readObject();
  				     
  				     
  				     
  				     Bid bid = new Bid();
  				     bid.type = type;
  				     bid.bid = bidValue;
  				     bid.check = checkB;
  				     bid.product_code = productCode;
  				     bid.userCode = userCode;
  				     System.out.println("Server-received: "+checkB.toString() + checkB.length);
  				     semaphore.acquire();
  				     	System.out.println("Server-received: Adding request to list");
  				     	requests.add(bid);
  				     semaphore.release();
  	  				System.out.println("Server-received: " + String.valueOf(productCode)+"  " + String.valueOf(bidValue)+"  " +checkB.toString()+"  "+ userCode);
  	    			DatagramPacket reply = new DatagramPacket("received".getBytes(), "received".length(), request.getAddress(), request.getPort());
  	    			aSocket.send(reply); 
  		         }
  		         else if(type == 'U')
  		         {
  		        	DatagramPacket reply = new DatagramPacket("updated".getBytes(), "updated".length(), request.getAddress(), request.getPort());
  	    			aSocket.send(reply); 
  		         }
			     
    		}
		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e) {System.out.println("IO: " + e.getMessage());
		}//finally {if(aSocket != null) aSocket.close();}
    }
	
}
