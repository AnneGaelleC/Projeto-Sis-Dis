package communication;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.Marshaller.Listener;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



public class ListenerTCP extends Thread{
    Socket s = null;
    
    public ListenerTCP(){
    	
    }
    public void setSocket(Socket socket )
    {
        s = socket;
    }
    public void run(){
        try {
        	//System.out.println("Listenning...");
             DataInputStream in = new DataInputStream( s.getInputStream());
             //s.setSoTimeout(3000);
            while(true)
            {
            	//String data = in.readUTF();	    // read a line of data from the stream
                //System.out.println("Listener TCP: "+ data) ;
            }
                
        }catch (SocketException e){System.out.println("(class ListenerTCP)Socket: " + e.getMessage());
        } catch (IOException ex) {
        Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
