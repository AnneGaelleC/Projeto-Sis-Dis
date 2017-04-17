package communication;

import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import auction.Bid_price_update;
import user.User;

import java.io.*;
public class UDPServer extends Thread {
	int serverPort;
	public static ArrayList< Bid_price_update > requests = new ArrayList< Bid_price_update >();
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
  				ByteArrayInputStream bis = new ByteArrayInputStream(request.getData());
  				ObjectInputStream ois = new ObjectInputStream(bis);
  		         char type = ois.readChar();
  		         if(type == 'B')
  		         {
  		        	 byte [] checkB;
  		        	 int productCode;
  		        	 int userCode;
  		        	 float bidValue;
  		        	 Bid_price_update bid = new Bid_price_update();
  		        	 productCode = ois.readInt();
  				     userCode= ois.readInt();
  				     bidValue = (float)ois.readObject();
  				     checkB = (byte[])ois.readObject();
  				     
  				     
  				     
  				     
  				     bid.type = type;
  				     bid.bid = bidValue;
  				     bid.check = checkB;
  				     bid.product_code = productCode;
  				     bid.userCode = userCode;
  				     semaphore.acquire();
  				     	System.out.println("Server-received bid: Adding request to list");
  				     	requests.add(bid);
  				     semaphore.release();
  	  				DatagramPacket reply = new DatagramPacket("bid received".getBytes(), "bid received".length(), request.getAddress(), request.getPort());
  	    			aSocket.send(reply); 
  		         }
  		         else if(type == 'U')
  		         {
  		        	 byte [] checkB;
 		        	 int productCode;
 		        	 int userCode;
 		        	 float value;
 		        	 String winnerName;
 		        	 Bid_price_update bid = new Bid_price_update();
 		        	 productCode = ois.readInt();
 				     userCode= ois.readInt();
 				     value = (float)ois.readObject();
 				     winnerName = (String)ois.readObject();
 				     checkB = (byte[])ois.readObject();
 				     
 				     bid.type = type;
 				     bid.bid = value;
 				     bid.check = checkB;
 				     bid.product_code = productCode;
 				     bid.userCode = userCode;
 				     bid.winnerName = winnerName;
 				     semaphore.acquire();
 				     	System.out.println("Server-received update: Adding request to list");
 				     	requests.add(bid);
 				     semaphore.release();
  		        	 DatagramPacket reply = new DatagramPacket("price updated".getBytes(), "price updated".length(), request.getAddress(), request.getPort());
  	    			 aSocket.send(reply); 
  		         }
  		         else if(type == 'E')
  		         {
  		        	byte [] checkB;
		        	 int productCode;
		        	 int userCode;
		        	 float value;
		        	 String winnerName;
		        	 Bid_price_update bid = new Bid_price_update();
		        	 productCode = ois.readInt();
				     userCode= ois.readInt();
				     value = (float)ois.readObject();
				     winnerName = (String)ois.readObject();
				     checkB = (byte[])ois.readObject();
				     
				     bid.type = type;
				     bid.bid = value;
				     bid.check = checkB;
				     bid.product_code = productCode;
				     bid.userCode = userCode;
				     bid.winnerName = winnerName;
				     semaphore.acquire();
				     	System.out.println("Server-received ending auction: Adding request to list");
				     	requests.add(bid);
				     semaphore.release();
 		        	 DatagramPacket reply = new DatagramPacket("action ended".getBytes(), "action ended".length(), request.getAddress(), request.getPort());
 	    			 aSocket.send(reply);
  		         }
  		         
  		         else if(type == 'A')
  		         {
  		        	DatagramPacket reply = new DatagramPacket("alive".getBytes(), "alive".length(), request.getAddress(), request.getPort());
	    			aSocket.send(reply);
  		         }
			     
    		}
		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e) {System.out.println("IO: " + e.getMessage());
		}//finally {if(aSocket != null) aSocket.close();}
    }
	
}
