package communication;

import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import auction.Bid;
import user.User;

import java.io.*;
public class TCPServer extends Thread {
	int serverPort;
	public static ArrayList< Bid > requests = new ArrayList< Bid >();
	public static Semaphore semaphore = new Semaphore(1);
	
	public TCPServer(){
		serverPort = 1235;
	}
	public void setServerPort(int port){
		serverPort = port;
	}
	
	public void run(){
		System.out.println("running server");
		ServerListener(serverPort);
	}

	public void ServerListener (int port) {
		try{
			ServerSocket listenSocket = new ServerSocket(port);
			while(true) {
				Socket clientSocket = listenSocket.accept();
				ConnectionServer c = new ConnectionServer(clientSocket);
			}
		} catch(IOException e) {System.out.println("Listen socket:"+e.getMessage());}
	}
	
}


//------------------------------------------------------------------------------------
class ConnectionServer extends Thread {
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	
	
	public ConnectionServer (Socket aClientSocket) {
		try {
			clientSocket = aClientSocket;
			in = new DataInputStream( clientSocket.getInputStream());
			out =new DataOutputStream( clientSocket.getOutputStream());
			this.start();
		} catch(IOException e) {System.out.println("Connection:"+e.getMessage());}
	}
	
	
	public void run(){
		try {			                 // an echo server
                while(true)
                {
                	Bid bid = new Bid();
                	int userCode = in.readInt();
                	int productCode = in.readInt();
                	float bidValue = in.readFloat();
                	String check = in.readUTF();
                	
                	bid.userCode = userCode;
                	bid.product_code = productCode;
                	bid.bid = bidValue;
                	bid.check = check;
                	System.out.println(userCode);
                	System.out.println(productCode);
                	System.out.println(bidValue);
                	System.out.println(check);
                	
                	TCPServer.semaphore.acquire();
                		TCPServer.requests.add(bid);
                	TCPServer.semaphore.release();
                    //String data = in.readUTF();	                  // read a line of data from the stream
                    //System.out.println("Recebido " + data);
                    out.writeUTF("received");
                    out.flush();
                }
		}catch (EOFException e){System.out.println("EOF:"+e.getMessage());
		} catch(IOException e) {System.out.println("readline:"+e.getMessage());
		} //finally{ try {clientSocket.close();}catch (IOException e){/*close failed*/}}
 catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
}
