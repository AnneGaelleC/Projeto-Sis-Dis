package user;
import cryptographia.*;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
//import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
//import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
//import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
//import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
//import java.util.Base64;
//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;
import java.util.Timer;
import java.util.TimerTask;

import communication.ConnectionManager;
import communication.UDPServer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import auction.Bid_price_update;
import auction.Product;

public class User {
	protected String name;
	protected int code;
	protected String myClientIp;
	private Chave cryptKey;
	protected ConnectionManager connectionManager;
	private int myServerPort;
	String PATH_CHAVE_PUBLICA;
	String PATH_CHAVE_PRIVADA;
	
	Timer timerCheckNewUser;
	Timer timerCheckServerRequests;
	
	ArrayList< Product > productsIamSellingList = new ArrayList< Product >();
	ArrayList< Product > AvailableProductsList= new ArrayList< Product >();
	ArrayList< Product > WantedProductsList = new ArrayList< Product >();
	
	ArrayList< User > othersUsersList = new ArrayList< User >();
	
	byte[] check;
	
	public ArrayList<User> getOthersUsersList() {
		return othersUsersList;
	}

	public void setOthersUsersList(ArrayList<User> othersUsersList) {
		this.othersUsersList = othersUsersList;
	}

	public int getMyServerPort() {
		return myServerPort;
	}

	public void setMyServerPort(int myServerPort) {
		this.myServerPort = myServerPort;
	}
	
	public static long getPID() {
	    String processName =
	      java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
	    return Long.parseLong(processName.split("@")[0]);
	  }
	
	public String getMyClientIp() {
		return myClientIp;
	}
	
	public void setMyClientIp(String myClientIp) {
		this.myClientIp = myClientIp;
	}

	public User(){
		name = "";
		code = 0;
		cryptKey = new Chave();
		othersUsersList.clear();
	}
	
	public ConnectionManager getconnectionManager() {
		return connectionManager;
	}
	
	public void setconnectionManager(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}
	
	public void setName(String name){
		this.name=name;
	}
	
	public void setCode(int code){
		this.code=code;
	}
	
	/**
	 * 
	 * @return this user name
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * 
	 * @return this user code
	 */
	public int getCode(){
		return code;
	}
	
	/**
	 * Method to initialize everythinf from an user
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public void initialization() throws ClassNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException{
		
		JFrame frame = new JFrame("User");
	    // prompt the user to enter his name
	    
	    while(this.name.isEmpty() == true){
	    	this.name = JOptionPane.showInputDialog(frame, "What's your name?", "Name", JOptionPane.QUESTION_MESSAGE);
	    	
	    	if(this.name == null){
	    		System.exit(0);
	    	}
	    	
	    	this.code=(int)name.hashCode();
	    	this.code = (int) (this.code + getPID());
	    	if(code<0){
	    		code = code*(-1);
	    	}
	    	this.code = this.code%10000; //this let the code with 4 digits
	    	
	    	PATH_CHAVE_PUBLICA = "./Key/"+ Integer.toString(this.code)+"public.key";
	    	PATH_CHAVE_PRIVADA = "./Key/"+ Integer.toString(this.code)+"private.key";
	    	//generate the public and private Key for criptographia
	    	if (!cryptKey.verificaSeExisteChavesNoSO(PATH_CHAVE_PUBLICA, PATH_CHAVE_PRIVADA))
	    	{
	    		cryptKey.geraChave(PATH_CHAVE_PUBLICA, PATH_CHAVE_PRIVADA);
	    	}
	    	
	    	connectionManager = new ConnectionManager();
	    	
	    	//tenta mandar uma mensagem de helo
			try {
				connectionManager.initConnections();
				myClientIp = connectionManager.getIp();
				myServerPort = connectionManager.getTcpServerPort();
				ObjectInputStream inputStream = null;
	    		inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PUBLICA));
	    		PublicKey publicKey = (PublicKey) inputStream.readObject();
	    		
	    		ObjectInputStream inputStreamPrivate = null;
	    		inputStreamPrivate = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PRIVADA));
	    		PrivateKey pk = (PrivateKey) inputStreamPrivate.readObject();
	    		
	    		check = cryptKey.sign("check", pk);
	    		setPublicKey(publicKey);
	            ByteArrayOutputStream bos = new ByteArrayOutputStream(10);
	            ObjectOutputStream oos = new ObjectOutputStream(bos);
	            oos.writeChar('H');
	            oos.writeObject(name);
	            oos.writeInt(code);
	            oos.writeObject(connectionManager.getIp());
	            oos.writeInt(myServerPort);
	            oos.writeObject(publicKey);
	            oos.flush();
	            byte[] output = bos.toByteArray();
				
				//Send the hello message when the user enter the multicast group
				connectionManager.sendMulticastMessage(output);
				inputStream.close();
				
				this.chekNewUsers(3);
				this.chekServerRequests(1);
				
			} catch (UnknownHostException | InvalidKeyException | SignatureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }	    
	}
	
	/**
	 * Method to this user sell a product on this auction
	 * @param newProduct product I want to sell
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 */
	public void SellNewProduct(Product newProduct, boolean realyNew) throws IOException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, SignatureException
	{
		ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PRIVADA));
		PrivateKey pk = (PrivateKey) inputStream.readObject();
		byte [] encrypted = cryptKey.sign(this.name, pk);
		
		newProduct.setAuthenticityCheck(encrypted.toString());
		if(realyNew == true)
			productsIamSellingList.add(newProduct);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream(10);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        
        oos.writeChar('N');
        oos.writeObject(name); //seller name
        oos.writeInt(code); //seller code
        oos.writeObject(newProduct.getProductName());
        oos.writeInt(newProduct.getProductCode());
        oos.writeObject(newProduct.getDescription());
        oos.writeFloat(newProduct.getInitialPrice());
        oos.writeInt(newProduct.getEndTime());
        oos.writeObject(this.getMyClientIp());
        oos.writeInt(connectionManager.getTcpServerPort());
        oos.writeObject(encrypted);
        oos.flush();
        byte[] output = bos.toByteArray();
        
        connectionManager.sendMulticastMessage(output);
	}
	
	
	/**
	 * Update the list of available products on this auction
	 */
	public void updateProductsList()
	{
		AvailableProductsList = connectionManager.getProductsList();
	}
	
	/**
	 * Get this user product's list
	 * @return
	 */
	public ArrayList<Product> getProductsList() 
	{
		return AvailableProductsList;
	}
	
	/*
	 * set a produc list for this user
	 */
	public void setProductsList(ArrayList<Product> list) 
	{
		AvailableProductsList = list;
	}
	
	/**
	 * Set a public key for this user
	 * @param pk new public key for this user
	 */
	public void setPublicKey(PublicKey pk)
	{
		cryptKey.setPublicKey(pk);
	}
	
	/**
	 * 
	 * @return this user public key
	 */
	public PublicKey getPublicKey()
	{
		return cryptKey.getPublicKey();
	}
	
	
	/**
	 * Method to check if there is new users on the auction
	 * @param seconds interval to check new users on the auction
	 */
	private void chekNewUsers(int seconds) {
		timerCheckNewUser = new Timer();
		//timerCheckNewUser.schedule(new sendMessagesToNewUsers(), seconds*1000);
		timerCheckNewUser.scheduleAtFixedRate(new sendMessagesToNewUsers(), 0, seconds*1000);
    }

	/**
	 * 
	 * @author lucas
	 * Class extend timer to send hello message to new users
	 */
    class sendMessagesToNewUsers extends TimerTask {
        public void run() {
            System.out.println("new Users check...");
            ArrayList< User > userList = new ArrayList< User >();
            userList = connectionManager.getUsersList();
        
            if(othersUsersList.size() != userList.size())
            {
            	System.out.println("New Users detected: sending hello message...");
            	
            	try {
                	System.out.println("sending hello message...");
    				ObjectInputStream inputStream = null;
    	    		inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PUBLICA));
    	    		PublicKey publicKey = (PublicKey) inputStream.readObject();
    	            ByteArrayOutputStream bos = new ByteArrayOutputStream(10);
    	            ObjectOutputStream oos = new ObjectOutputStream(bos);
    	            oos.writeChar('H');
    	            oos.writeObject(name);
    	            oos.writeInt(code);
    	            oos.writeObject(connectionManager.getIp());
    	            oos.writeInt(connectionManager.getTcpServerPort());
    	            oos.writeObject(publicKey);
    	            oos.flush();
    	            byte[] output = bos.toByteArray();
    	            connectionManager.sendMulticastMessage(output);
    				inputStream.close();
    				//userList.get(0).setOthersUsersList(userList);
    				//othersUsersList = userList;
    	            
    				
    				//Send the hello message when the user enter the multicast group
    				
    				
    			} catch (UnknownHostException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (ClassNotFoundException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            	
            	//I send my hello again
            	for(int i = 0; i<userList.size(); i++)
	            {
	            	if(!othersUsersList.contains(userList.get(i)))
	            	{
	            		connectionManager.createNewClietForMe(userList.get(i).getMyClientIp(), userList.get(i).getMyServerPort());
	            	}
	            }
	            
	            othersUsersList.clear();
	            for(int i = 0; i<userList.size(); i++)
	            {
	            	othersUsersList.add(userList.get(i));
	            }
	            
	            //I send my products again if there is a new user
	            for(int j = 0; j< productsIamSellingList.size(); j++)
	            {
	            	try {
						SellNewProduct(productsIamSellingList.get(j), false);
					} catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException
							| SignatureException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
            	
            }
            
            
            
            //Check if the auction is ended
            for(int h=0; h<productsIamSellingList.size(); h++)
			{
				if(productsIamSellingList.get(h).isActivatedAuction() == false)
				{
					ByteArrayOutputStream bos = new ByteArrayOutputStream(100);
		            ObjectOutputStream oos;
					try {
						oos = new ObjectOutputStream(bos);
					
			            oos.writeChar('E');
						oos.writeInt(productsIamSellingList.get(h).getProductCode());
						oos.writeInt(code);
						oos.writeObject(productsIamSellingList.get(h).getCurrentPrice());
						oos.writeObject(productsIamSellingList.get(h).getAuctionWinnerName());
						oos.writeObject(check);
						oos.flush();
			            byte[] output = bos.toByteArray();
			            
			            for(int j = 0; j< productsIamSellingList.get(h).getInterrestedUserSize(); j++)
			            {
			            	for(int k=0; k< othersUsersList.size(); k++)
			            	{
			            		if(productsIamSellingList.get(h).getInterrestedUserAt(j) == othersUsersList.get(k).getCode())
			            		{
			            			connectionManager.sendPriceUpdate(othersUsersList.get(k).getMyClientIp(), othersUsersList.get(k).getMyServerPort(), output);
			            		}
			            	}
			            }
			            productsIamSellingList.remove(h);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		            
				}
			}
            
            boolean isAlive = false;
            for(int i = 0 ; i < WantedProductsList.size() ; i++)
            {
            	for(int j = 0 ; j < othersUsersList.size() ; j++)
            	{
            		if(WantedProductsList.get(i).getSellerCode() == othersUsersList.get(j).getCode())
            		{
            			ByteArrayOutputStream bos = new ByteArrayOutputStream(100);
    		            ObjectOutputStream oos;
						try {
							 oos = new ObjectOutputStream(bos);
							 oos.writeChar('A');
							 oos.writeObject(check);
							 oos.flush();
					         byte[] output = bos.toByteArray();
		            			//ask if its server is alive
					         isAlive = connectionManager.sendIsAliveMessage(othersUsersList.get(j).getMyClientIp(), othersUsersList.get(j).getMyServerPort(), output);
						
					         if(isAlive == false)
					         {
					        	 //Server is dead
					        	 System.out.println("server has died");
					        	 WantedProductsList.get(i).finishByServerDead();
					        	 JOptionPane.showMessageDialog(null, "(Auctioner died...)ACTION ENDED for product ctype filter textode: " + WantedProductsList.get(i).getProductCode(), "ACTION ENDED", JOptionPane.INFORMATION_MESSAGE);
					        	 WantedProductsList.remove(i);
					        	 break;
					        	 
					         }
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
            		}
            	}
            }
        }
    }
    
    /**
     * Method to verify a signature
     * @param userName : string to compare with the decripted signature String
     * @param pk : public key to try verify the signature
     * @param signature : byte array with the signed string
     * @return true is the signature was confirmed 
     * @throws Exception
     */
    public boolean checkAuthenticity(String userName, PublicKey pk, byte[] signature) throws Exception
    {
    		return cryptKey.signatureCheck(userName.getBytes(), pk, signature);
    }
    
    /**
     *  timer to check new requests to my server
     * @param seconds: interval to check new requests to my server
     */
    private void chekServerRequests(int seconds) {
		timerCheckServerRequests = new Timer();
		//timerCheckNewUser.schedule(new sendMessagesToNewUsers(), seconds*1000);
		timerCheckServerRequests.scheduleAtFixedRate(new ProcessRequests(), 0, seconds*1000);
    }
    
    /**
     * 
     * @author lucas
     * Timer to process the requests to my server
     */
    class ProcessRequests extends TimerTask
    {
    	ArrayList< Bid_price_update > requests = new ArrayList< Bid_price_update >();
    	public void run() {
    		//System.out.println("checking requests");
    		try {
				UDPServer.semaphore.acquire();
					requests.clear();
					for(int i = 0; i< UDPServer.requests.size(); i++)
					{
						requests.add(UDPServer.requests.get(i));
					}
					UDPServer.requests.clear();	
				UDPServer.semaphore.release();
				
				if(!requests.isEmpty())
				{
					System.out.println("User-ProcessRequests: Processing...");
					for(int i = 0; i< requests.size(); i++)
					{
						if(requests.get(i).type == 'B')
						{
							for(int j = 0; j < othersUsersList.size(); j++)
							{
								if(requests.get(i).userCode == othersUsersList.get(j).getCode())
								{
									/**
									 * DEBUG...
									 */
									System.out.println("User-ProcessRequests: User founded");
									
									PublicKey publick = othersUsersList.get(j).getPublicKey();
		
									if(checkAuthenticity("check", publick, requests.get(i).check) == true)
									{
										System.out.println("User-ProcessRequests: Signature checked");
										//if signature checked
										for(int k = 0; k< productsIamSellingList.size(); k++)
										{	
											if(productsIamSellingList.get(k).getProductCode() == requests.get(i).product_code)
											{
												productsIamSellingList.get(k).addInterestedUser(othersUsersList.get(j).getCode());
												if(productsIamSellingList.get(k).getCurrentPrice() < requests.get(i).bid && productsIamSellingList.get(k).isActivatedAuction())
												{
													//update the current price of this product
													productsIamSellingList.get(k).setCurrentPrice(requests.get(i).bid);
													productsIamSellingList.get(k).setAuctionWinnerCode(othersUsersList.get(j).getCode());
													productsIamSellingList.get(k).setAuctionWinnerName(othersUsersList.get(j).getName());
													System.out.println("current price updated");
												}
												for(int m = 0; m< productsIamSellingList.get(k).getInterrestedUserSize(); m++ )
												{
													for(int l = 0; l < othersUsersList.size(); l++)
													{
														if(productsIamSellingList.get(k).getInterrestedUserAt(m) == othersUsersList.get(l).getCode())
														{
															ByteArrayOutputStream bos = new ByteArrayOutputStream(100);
												            ObjectOutputStream oos = new ObjectOutputStream(bos);
												            oos.writeChar('U');
															oos.writeInt(requests.get(i).product_code);
															oos.writeInt(code);
															oos.writeObject(productsIamSellingList.get(k).getCurrentPrice());
															oos.writeObject(productsIamSellingList.get(k).getAuctionWinnerName());
															oos.writeObject(check);
															oos.flush();
												            byte[] output = bos.toByteArray();
												            
															connectionManager.sendPriceUpdate(othersUsersList.get(l).getMyClientIp(), othersUsersList.get(l).getMyServerPort(), output);
														}
													}
												}
											}
										}
									}
									else
									{
										System.out.println("User-ProcessRequests: Bad signature...");
									}
								}
							}
						}
						else if(requests.get(i).type == 'U')
						{
							for(int t=0; t< WantedProductsList.size(); t++)
					    	{
					    		if(WantedProductsList.get(t).getProductCode() == requests.get(i).product_code)
					    		{
					    			WantedProductsList.get(t).setCurrentPrice(requests.get(i).bid);
					    			WantedProductsList.get(t).setAuctionWinnerName(requests.get(i).winnerName);
					    			JOptionPane.showMessageDialog(null, "Price updated to product code: " + requests.get(i).product_code , "PriceUpdate ", JOptionPane.INFORMATION_MESSAGE);
					    		}
					    	}
						}
						else if(requests.get(i).type == 'E')
						{
							for(int t=0; t< WantedProductsList.size(); t++)
					    	{
					    		if(WantedProductsList.get(t).getProductCode() == requests.get(i).product_code)
					    		{
					    			WantedProductsList.get(t).setCurrentPrice(requests.get(i).bid);
					    			WantedProductsList.get(t).setAuctionWinnerName(requests.get(i).winnerName);
					    			JOptionPane.showMessageDialog(null, "ACTION ENDED for product code: " + requests.get(i).product_code +" WINNER: "+ requests.get(i).winnerName, "ACTION ENDED", JOptionPane.INFORMATION_MESSAGE);
					    		}
					    	}
						}
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    

    public void SendBidByUDP(int productCode, int sellerCode, float bidValue) throws FileNotFoundException, IOException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, SignatureException
    {
    	for(int i = 0; i< AvailableProductsList.size(); i++)
    	{
    		this.updateProductsList();
    		if(AvailableProductsList.get(i).getProductCode() == productCode && AvailableProductsList.get(i).getSellerCode() == sellerCode)
    		{
    			System.out.println("(User)enviando bid...");
    			byte[] check;
    			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PRIVADA));
    			PrivateKey pk = (PrivateKey) inputStream.readObject();
    			
    			check = cryptKey.sign("check", pk);
    			
    			ByteArrayOutputStream bos = new ByteArrayOutputStream(100);
	            ObjectOutputStream oos = new ObjectOutputStream(bos);
	            oos.writeChar('B');
				oos.writeInt(productCode);
				oos.writeInt(code);
				oos.writeObject(bidValue);
				oos.writeObject(check);
				oos.flush();
	            byte[] output = bos.toByteArray();
	            
	            addWantedProduct(AvailableProductsList.get(i));
    			connectionManager.sendBid(AvailableProductsList.get(i).getSellerIp(), AvailableProductsList.get(i).getSellerPort(), output);
    		}
    	}
    }
    
    public ArrayList<Product> getWantedProductsList() {
		return WantedProductsList;
	}
    
    public void addWantedProduct(Product p)
    {
    	for(int i=0; i< WantedProductsList.size(); i++)
    	{
    		if(WantedProductsList.get(i).getProductCode() == p.getProductCode())
    		{
    			return;
    		}
    	}
    	WantedProductsList.add(p);
    }
    
}//end class