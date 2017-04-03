package auction;

public class Product {

    private String name;
    private float price;
    private String description;
    
    
    
    /*Metodos*/
    /**
    * Get the product's name 
    * @return      the name of the product
    */
    public String getName() {
        return name;
    }

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
    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}