package auction;
import java.util.Timer;
import java.util.TimerTask;

public class Product {

	/**
	 * Name of the product
	 */
    private String productName;
    /**
     * code of this product
     */
    private int productCode;
    /**
     * Name of the user that is selling this product
     */
    private String sellerName;
    /**
     * code of the user that is selling this product
     */
    private int sellerCode;
    /**
     * The initial price of this product. The seller define this value
     */
    private float initialPrice;
    /**
     * The current price of this product updated by the bids
     */
    private float currentPrice;
    /**
     * A little description of this product, explaining its characteristics
     */
    private String description;
    /**
     * the unicast ip of the seller
     */
    private String sellerIp;
    /**
     * time the auction of this product will be opend
     */
    private int endTime;
    /**
     * this text will be encrypted with the private key of the buyer to authenticate his bid
     * this string will be the name of the buyer encrypted.
     * After decrypted it will be compared with the name of the buyer to authenticate the bid
     */
    private String authenticityCheck;
    
    /**
     * Timer to count the auction time
     */
    Timer timer;
    
    boolean activatedAuction;
    
    //****************************************************************************************
    
    /**
     * Constructor of this class: initializes it's attributes with default value
     */
    public Product(){
    	activatedAuction = false;
    }
    /**
     * get the seller's IP
     * @return sellerIp : unicast ip of the seller
     */
    public String getSellerIp() {
		return sellerIp;
	}

    /**
     * 
     * @param sellerIp: new value to ip of the seller of this product
     */
	public void setSellerIp(String sellerIp) {
		this.sellerIp = sellerIp;
	}

    public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		activatedAuction =true;
		this.endTime = endTime;
		//finishAuction(endTime);
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public int getProductCode() {
		return productCode;
	}

	public void setProductCode(int productCode) {
		this.productCode = productCode;
	}

	public int getSellerCode() {
		return sellerCode;
	}

	public void setSellerCode(int sellerCode) {
		this.sellerCode = sellerCode;
	}

    public float getInitialPrice() {
        return initialPrice;
    }

    public String getDescription() {
        return description;
    }

    /**
    * set the name of the product that is being sold
    * @param  name  new name for this product
    */
   
    public void setInitialPrice(float price) {
        this.initialPrice = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    

    private void finishAuction(int minutes) {
        timer = new Timer();
        timer.schedule(new RemindTask(), minutes*1000*60);
    }

    class RemindTask extends TimerTask {
        public void run() {
            System.out.println("Action ended");
            activatedAuction = false;
            timer.cancel(); //Terminate the timer thread
        }
    }

	public String getAuthenticityCheck() {
		return authenticityCheck;
	}
	public void setAuthenticityCheck(String authenticityCheck) {
		this.authenticityCheck = authenticityCheck;
	}
    
    
}