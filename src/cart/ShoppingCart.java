package cart;

import model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class ShoppingCart {

	private static ShoppingCart INSTANCE;
	private Map<Integer, CartEntry> entries;

	public static ShoppingCart getInstance() {
		if (INSTANCE == null) {
	        INSTANCE = new ShoppingCart();
	    } 
	    return INSTANCE;
	}

	public ShoppingCart() {
		this.entries = new HashMap<>();
	}

	public void addProduct(Product product) {
		CartEntry entry = entries.get(product.getId());

		if (entry != null) {
			entry.increaseQuantity();
		} else {
			CartEntry newEntry = new CartEntry(product, 1);
			entries.put(product.getId(), newEntry);
		}
	}

	public void removeProduct(Product product) {
		CartEntry entry = entries.get(product.getId());

		if (entry != null) {
			entry.decreaseQuantity();
		}
	}

	public int getQuantity(Product product) {
		CartEntry entry = entries.get(product.getId());
		return (entry != null) ? entry.getQuantity() : 0;
	}

	public float calculateTotal() {
		float total = 0;
		for (CartEntry entry : entries.values()) {
			total += entry.getProduct().getPrice() * entry.getQuantity();
		}
		return Math.round(total * 100.0) / 100.0f;
	}

	public List<CartEntry> getEntries() {
		return new ArrayList<>(entries.values());
	}

	public void clear() {
		entries.clear();
	}

	public void setProductQuantity(Product product, int quantity) {
		
		if (quantity <= 0) {
			entries.remove(product.getId());
		} else {
			
			CartEntry entry = entries.get(product.getId());
			if (entry != null) {
				entry.setQuantity(quantity);
			} else {
				CartEntry newEntry = new CartEntry(product, quantity);
				entries.put(product.getId(), newEntry);
			}
		}
	}
}