package model;

import java.util.Date;
import java.util.List;

public class Order {
    private int id;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private double totalAmount;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private List<OrderItem> items; // Danh sách sản phẩm trong đơn hàng

    // 🔹 Constructor mặc định
    public Order() {}

    // 🔹 Constructor đầy đủ (không bao gồm danh sách items)
    public Order(int id, String customerName, String customerEmail, String customerPhone,
                 double totalAmount, String status, Date createdAt, Date updatedAt) {
        this.id = id;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // 🔹 Constructor đầy đủ (bao gồm danh sách items)
    public Order(int id, String customerName, String customerEmail, String customerPhone,
                 double totalAmount, String status, Date createdAt, Date updatedAt,
                 List<OrderItem> items) {
        this(id, customerName, customerEmail, customerPhone, totalAmount, status, createdAt, updatedAt);
        this.items = items;
    }

    // ===========================
    // 🔸 GETTERS & SETTERS
    // ===========================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    // ===========================
    // 🔸 Helper method (optional)
    // ===========================
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", customerPhone='" + customerPhone + '\'' +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", items=" + (items != null ? items.size() : 0) +
                '}';
    }
}
