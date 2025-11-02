package model;

import java.util.Date;

public class Product {
    private int id;
    private String name;
    private String category;
    private double price;
    private int quantity;
    private String description;
    private int collectionId;
    private Date createdAt;
    private Date updatedAt;

    public Product() {}

    public Product(int id, String name, String category, double price, int quantity,
                   String description, int collectionId, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
        this.collectionId = collectionId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters & Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getDescription() { return description; }
    public int getCollectionId() { return collectionId; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setDescription(String description) { this.description = description; }
    public void setCollectionId(int collectionId) { this.collectionId = collectionId; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
