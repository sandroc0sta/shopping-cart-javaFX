package cart;

import model.Product;

public class CartEntry {
    private Product product;
    private int quantity;

    public CartEntry(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void increaseQuantity() {
        this.quantity++;
    }

    public void decreaseQuantity() {
        if (this.quantity > 0) {
            this.quantity--;
        }
    }
    
    public void setQuantity(int quantity) {
    	if(quantity >=0) {
    		this.quantity = quantity;
    	}
    }
}