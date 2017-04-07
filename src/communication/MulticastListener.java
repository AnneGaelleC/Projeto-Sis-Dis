package communication;

import java.net.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.util.ArrayList;

import auction.Product;
import user.Seller;
import user.User;

public class MulticastListener extends Thread{
    MulticastSocket s = null;
    ArrayList< User > userList = new ArrayList< User >();
    ArrayList< Product > productsList = new ArrayList< Product >();
    
    public ArrayList<Product> getProductsList() {
		return productsList;
	}

	String myIp;
    public ArrayList<User> getUserList() {
		return userList;
	}
	public void setUserList(ArrayList<User> userList) {
		this.userList = userList;
	}
	public String getMyIp() {
		return myIp;
	}
	public void setMyIp(String myIp) {
		this.myIp = myIp;
	}
	public void setMulticastSocket(MulticastSocket socket )
    {
        s = socket;
    }
    public void run(){
        
        try {
            while(true){
                byte[] buffer = new byte[10000];
                DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
                s.receive(messageIn);
                try {
					processMessages(messageIn);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
                
        }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
        } catch (IOException ex) {
        	System.out.println(ex.toString());
        }
    }
    
    /**
     * 
     * @param messageIn
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void processMessages(DatagramPacket messageIn) throws IOException, ClassNotFoundException
    {
    	ByteArrayInputStream bis = new ByteArrayInputStream(messageIn.getData());
        ObjectInputStream ois = new ObjectInputStream(bis);
        
        //System.out.print(ois.available());
        char messageType = ois.readChar();
        
        if(messageType == 'H')
        {
        	String name, ip;
        	PublicKey publicKey;
        	int code;
        	name = (String) ois.readObject();
			code = ois.readInt();
			ip = (String) ois.readObject();
			publicKey =  (PublicKey)ois.readObject();		
			User newUser = new User();
			newUser.setName(name);
			newUser.setCode(code);
			newUser.setMyClientIp(ip);
			//newUser.setPublicKey(publicKey);
			
			if(ip != myIp)
				userList.add(newUser);	
        }
        
        if(messageType == 'N')
        {
        	String productName, sellerName, ip, description;
        	int sellerCode, productCode;
        	float price, endTime;
        	Product product = new Product();
        	
        	sellerName = (String) ois.readObject();
        	product.setSellerName(sellerName);
        	System.out.println(sellerName);
     
        	sellerCode = ois.readInt();
        	product.setSellerCode(sellerCode);
        	System.out.println(sellerCode);
        	
        	productName = (String) ois.readObject();
        	product.setProductName(productName);
        	System.out.println(productName);
        	
        	productCode = ois.readInt();
        	product.setProductCode(productCode);
        	System.out.println(productCode);
        	
        	description = (String) ois.readObject();
        	product.setDescription(description);
        	System.out.println(description);
        	
        	price = ois.readFloat();
        	product.setPrice(price);
        	System.out.println(price);
        	
        	endTime = ois.readFloat();
        	product.setEndTime(endTime);
        	System.out.println(endTime);
        	
        	ip = (String) ois.readObject();
        	product.setSellerIp(ip);
        	System.out.println(ip);
        	
        	//if(ip != myIp)
        		productsList.add(product);
        }
        
        if(messageType == 'U')//update prices
        {
        	
        }
    }
}

