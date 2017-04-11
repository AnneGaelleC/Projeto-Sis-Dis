package communication;

import java.net.*;
import java.io.*;
public class UDPClient {
	int Port;
	String Ip;
	Socket s = null;
	ListenerTCP listener;
	DataOutputStream out;
	
	DatagramSocket aSocket = null;
	InetAddress aHost;
	public UDPClient(){
		
	}
	public void Connect (int port, String ServerIp) {
		try {
			//Ip =ServerIp;
			aSocket = new DatagramSocket();  
			aHost = InetAddress.getByName(ServerIp);
			//serverPort = port;	
			aSocket.setSoTimeout(3000);
           

		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e){System.out.println("IO: " + e.getMessage());
		}//finally {if(aSocket != null) aSocket.close();}
     }
	
	public void SenMessage(byte[] messagetoSend) throws IOException
	{
		 String message = messagetoSend.toString();
		 DatagramPacket request = new DatagramPacket(messagetoSend,  message.length(), aHost, Port);
		 aSocket.send(request);
		 byte[] buffer = new byte[10000];
		 DatagramPacket reply = new DatagramPacket(buffer, buffer.length);	
		 try
		 {
		     aSocket.receive(reply);
		     System.out.println("Reply: " + new String(reply.getData()));
		 }
		 catch(SocketTimeoutException e){
		     System.out.println("-->>Timed out after 3 seconds!!");
		 }
	}
	
	public int getPort() {
		return Port;
	}

	public void setPort(int myPort) {
		this.Port = myPort;
	}

	public String getIp() {
		return Ip;
	}

	public void setIp(String myIp) {
		this.Ip = myIp;
	}
}
