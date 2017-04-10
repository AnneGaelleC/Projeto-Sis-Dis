package communication;

import java.net.*;
import java.io.*;
public class TCPClient {
	int myPort;
	String myIp;
	Socket s = null;
	ListenerTCP listener;
	
	
	public TCPClient(){
		
	}
	public void Connect (int port, String ServerIp) {
		// arguments supply message and hostname
                
		try{
			listener = new ListenerTCP();
			int serverPort = port;
			s = new Socket(ServerIp, serverPort);  
			listener.setSocket(s);
			listener.start();
			System.out.println("Socket Ok");
            //s.setSoTimeout(3000);
		}catch (UnknownHostException e){System.out.println("Socket:"+e.getMessage());
		}catch (EOFException e){System.out.println("EOF:"+e.getMessage());
		}catch (IOException e){System.out.println("readline:"+e.getMessage());
		}finally {if(s!=null) try {s.close();}catch (IOException e){System.out.println("close:"+e.getMessage());}}
     }
	
	public void SenMessage(String messagetoSend) throws IOException
	{
			System.out.println("sending message");
        	DataOutputStream out =new DataOutputStream( s.getOutputStream());
            out.writeUTF(messagetoSend);      	// UTF is a string encoding see Sn. 4.4
	}
	
	public int getMyPort() {
		return myPort;
	}

	public void setMyPort(int myPort) {
		this.myPort = myPort;
	}

	public String getMyIp() {
		return myIp;
	}

	public void setMyIp(String myIp) {
		this.myIp = myIp;
	}
}
