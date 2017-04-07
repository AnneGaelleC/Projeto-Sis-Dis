package auction;

public class Product {

    private String productName;
    private String sellerName;
    private int productCode;
    private int sellerCode;
    private float price;
    private String description;
    private float endTime;
    private String sellerIp;
    
    public String getSellerIp() {
		return sellerIp;
	}

	public void setSellerIp(String sellerIp) {
		this.sellerIp = sellerIp;
	}

	public Product(){
    	
    }
    
    public float getEndTime() {
		return endTime;
	}

	public void setEndTime(float endTime) {
		this.endTime = endTime;
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

    /*Metodos*/
    /**
    * Get the product's name 
    * @return      the name of the product
    */

    public float getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    /**
    * set the name of the product that is being sold
    * @param  name  new name for this product
    */
   
    public void setPrice(float price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}