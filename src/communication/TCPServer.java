package communication;

import java.net.*;
import java.io.*;
public class TCPServer extends Thread {
	int serverPort;
	
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
                	int userCode = in.readInt();
                	int productCode = in.readInt();
                	float bid = in.readFloat();
                	String check = in.readUTF();
                    //String data = in.readUTF();	                  // read a line of data from the stream
                    //System.out.println("Recebido " + data);
                    out.writeUTF("received");
                    out.flush();
                }
		}catch (EOFException e){System.out.println("EOF:"+e.getMessage());
		} catch(IOException e) {System.out.println("readline:"+e.getMessage());
		} //finally{ try {clientSocket.close();}catch (IOException e){/*close failed*/}}
		

	}
}
