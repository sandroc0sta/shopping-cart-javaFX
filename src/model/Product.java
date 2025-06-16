package model;

public class Product {
	private int id;
	private String name;
	private float price;
	private String imagePath;


	public Product(int id, String name, float price, String imagePath) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.imagePath= imagePath;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public float getPrice() {
		return price;
	}
	
	public String getImagePath() {
        return imagePath;
    }
	
	public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}