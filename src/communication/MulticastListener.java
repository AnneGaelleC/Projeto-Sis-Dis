package communication;

import java.net.*;
import java.io.*;

public class MulticastListener extends Thread{
    MulticastSocket s = null;
    
    public void setMulticastSocket(MulticastSocket socket )
    {
        s = socket;
    }
    public void run(){
        
        try {
            while(true){
                byte[] buffer = new byte[1000];
                DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
                s.receive(messageIn);
                System.out.println("Received:" + new String(messageIn.getData()));
            }
                
        }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
        } catch (IOException ex) {
        	System.out.println(ex.toString());
        }
    }
}

