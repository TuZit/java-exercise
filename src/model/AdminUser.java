package model;

import java.util.Date;

public class AdminUser {
    private int id;
    private String username;
    private String passwordHash;
    private String role;
    private Date createdAt;

    public AdminUser() {}

    public AdminUser(int id, String username, String passwordHash, String role, Date createdAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public Date getCreatedAt() { return createdAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRole(String role) { this.role = role; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
