package communication;

import java.net.*;
import java.io.*;
public class TCPClient {
	int myPort;
	String myIp;
	Socket s = null;
	
	public void Connect (int port, String ServerIp) {
		// arguments supply message and hostname
		
                ListenerTCP listener = new ListenerTCP();
		try{
			int serverPort = port;
			s = new Socket(ServerIp, serverPort);    
                        //s.setSoTimeout(3000);
                        listener.setSocket(s);
                        listener.start();
                        
                        
		}catch (UnknownHostException e){System.out.println("Socket:"+e.getMessage());
		}catch (EOFException e){System.out.println("EOF:"+e.getMessage());
		}catch (IOException e){System.out.println("readline:"+e.getMessage());
		}finally {if(s!=null) try {s.close();}catch (IOException e){System.out.println("close:"+e.getMessage());}}
     }
	
	public void SenMessage(String messagetoSend) throws IOException
	{
        while(!messagetoSend.startsWith("exit"))
        {
        	DataOutputStream out =new DataOutputStream( s.getOutputStream());
            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
            messagetoSend = bufferRead.readLine();
            out.writeUTF(messagetoSend);      	// UTF is a string encoding see Sn. 4.4
        }
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
