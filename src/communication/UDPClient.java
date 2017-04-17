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
	boolean serverAlive;
	public UDPClient(){
		
	}
	public void Connect (int port, String ServerIp) {
		try {
			//Ip =ServerIp;
			aSocket = new DatagramSocket();  
			aHost = InetAddress.getByName(ServerIp);
			//serverPort = port;	
			aSocket.setSoTimeout(5000);
           

		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e){System.out.println("IO: " + e.getMessage());
		}//finally {if(aSocket != null) aSocket.close();}
     }
	
	public void SenMessage(byte[] messagetoSend)
	{
		 String message = messagetoSend.toString();
		 DatagramPacket request = new DatagramPacket(messagetoSend,  messagetoSend.length, aHost, Port);
		 try {
			aSocket.send(request);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 byte[] buffer = new byte[1000];
		 DatagramPacket reply = new DatagramPacket(buffer, buffer.length);	
		 try
		 {
		     aSocket.receive(reply);
		     System.out.println("Reply: " + new String(reply.getData()));
		     serverAlive = true;
		 }
		 catch(SocketTimeoutException e){
		     System.out.println("-->>Timed out after 3 seconds!!");
		     serverAlive = false;
		 } catch (IOException e) {
			System.out.println("UDPClient-SenMessage: erro ao receber reply");
			serverAlive = false;
			e.printStackTrace();
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
	public boolean isServerAlive() {
		return serverAlive;
	}
	
	public boolean SendMessageAlive(byte[] messagetoSend)
	{
		 String message = messagetoSend.toString();
		 DatagramPacket request = new DatagramPacket(messagetoSend,  messagetoSend.length, aHost, Port);
		 try {
			aSocket.send(request);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 byte[] buffer = new byte[1000];
		 DatagramPacket reply = new DatagramPacket(buffer, buffer.length);	
		 try
		 {
		     aSocket.receive(reply);
		     System.out.println("Reply: " + new String(reply.getData()));
		     return serverAlive = true;
		 }
		 catch(SocketTimeoutException e){
		     System.out.println("-->>Timed out after 3 seconds!!");
		     return serverAlive = false;
		 } catch (IOException e) {
			System.out.println("UDPClient-SenMessage: erro ao receber reply");
			e.printStackTrace();
			return serverAlive = false;
		}
	}
	
}
