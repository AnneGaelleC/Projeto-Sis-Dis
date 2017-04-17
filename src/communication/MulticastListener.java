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
import user.User;

public class MulticastListener extends Thread{
    MulticastSocket s = null;
    ArrayList< User > userList = new ArrayList< User >();
    ArrayList< Product > productsList = new ArrayList< Product >();
    String myIp;
    
    public ArrayList<Product> getProductsList() {
		return productsList;
	}
	
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
				} catch (Exception e) {
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
     * @throws Exception 
     */
    public void processMessages(DatagramPacket messageIn) throws Exception
    {
    	ByteArrayInputStream bis = new ByteArrayInputStream(messageIn.getData());
        ObjectInputStream ois = new ObjectInputStream(bis);
        
        char messageType = ois.readChar();
        
        if(messageType == 'H')
        {
        	String name, ip;
        	PublicKey publicKey;
        	int code, serverPort;
        	name = (String) ois.readObject();
			code = ois.readInt();
			ip = (String) ois.readObject();
			serverPort = ois.readInt();
			publicKey =  (PublicKey)ois.readObject();
			User newUser = new User();
			newUser.setName(name);
			newUser.setCode(code);
			newUser.setMyClientIp(ip);
			newUser.setMyServerPort(serverPort);
			newUser.setPublicKey(publicKey);
			
			int i = 0;
			boolean equal = false;
			for(i = 0; i < userList.size(); i++)
            {
				if(userList.get(i).getCode() == code)
				{
					equal = true;
					break;
				}
            }
			if(equal == false)
			{
				userList.add(newUser);
			}
			//to add myself to my list
			if( userList.size() == 0)
			{
				userList.add(newUser);
			}
			/*if(!userList.contains(newUser))
			{
				userList.add(newUser);	
			}*/
        }
        
        if(messageType == 'N')
        {
        	String productName, sellerName, ip, description;
        	int sellerCode, productCode,endTime, sellerPort;
        	float price;
        	Product product = new Product();
        	byte [] authenticity;
        	
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
        	product.setInitialPrice(price);
        	System.out.println(price);
        	
        	endTime = ois.readInt();
        	product.setEndTime(endTime);
        	System.out.println(endTime);
        	
        	ip = (String) ois.readObject();
        	product.setSellerIp(ip);
        	System.out.println(ip);
        	
        	sellerPort = ois.readInt();
        	product.setSellerPort(sellerPort);
        	System.out.println(sellerPort);
        	
        	authenticity = (byte[])ois.readObject();
        	product.setAuthenticityCheck(authenticity.toString());
        	System.out.println(authenticity);
        	
        	for(int i = 0; i<userList.size(); i++)
        	{
        		if(userList.get(i).getCode() == sellerCode)
        		{
        			PublicKey pk = userList.get(i).getPublicKey();
        			if(userList.get(0).checkAuthenticity(sellerName, pk, authenticity) /*&& !productsList.contains(product)*/)
        			{
        				boolean add = true;
        				for(int k=0; k<productsList.size(); k++)
        				{
        					if(productsList.get(k).getProductCode() == product.getProductCode() && productsList.get(k).getSellerCode() == product.getSellerCode())
        						add = false;
        				}
        				if(add == true)
        				{
	                		productsList.add(product);
	                		userList.get(0).setProductsList(productsList);
	                		System.out.println("Sign verified. Adding the product to list...");
        				}
                		break;
        			}
        			else
        			{
        				System.out.println("bad sign... or product already registered ");
            			
        			}
        		}
        	}
        	
        }
        
        if(messageType == 'U')//update prices
        {
        	
        }
    }
}

