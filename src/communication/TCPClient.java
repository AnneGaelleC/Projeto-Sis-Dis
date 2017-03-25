package communication;

import java.net.*;
import java.io.*;
public class TCPClient {
	public void Connect (int port, String ServerIp) {
		// arguments supply message and hostname
		Socket s = null;
                ListenerTCP listener = new ListenerTCP();
                String messagetoSend;
		try{
			int serverPort = 7896;
			s = new Socket(ServerIp, serverPort);    
                        //s.setSoTimeout(3000);
                        listener.setSocket(s);
                        listener.start();
			DataOutputStream out =new DataOutputStream( s.getOutputStream());
                        messagetoSend = ""; 
                        while(!messagetoSend.startsWith("exit"))
                        {
                            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
                            messagetoSend = bufferRead.readLine();
                            out.writeUTF(messagetoSend);      	// UTF is a string encoding see Sn. 4.4
                        }
		}catch (UnknownHostException e){System.out.println("Socket:"+e.getMessage());
		}catch (EOFException e){System.out.println("EOF:"+e.getMessage());
		}catch (IOException e){System.out.println("readline:"+e.getMessage());
		}finally {if(s!=null) try {s.close();}catch (IOException e){System.out.println("close:"+e.getMessage());}}
     }
}
