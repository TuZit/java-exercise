package model;

import java.util.Date;
import java.util.List;

public class Order {
    private int id;
    private int customerId;
    private double totalAmount;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private List<OrderItem> items;
    private Customer customer; // To hold joined customer data

    public Order() {}

    // Constructor for creating a new order
    public Order(int customerId, double totalAmount, String status, List<OrderItem> items) {
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.items = items;
    }

    // Constructor for retrieving an order from DB
    public Order(int id, int customerId, double totalAmount, String status, Date createdAt, Date updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
}

