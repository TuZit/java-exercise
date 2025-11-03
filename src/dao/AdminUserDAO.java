package dao;

import model.AdminUser;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminUserDAO {
    private Connection connection;

    public AdminUserDAO(Connection connection) {
        this.connection = connection;
    }

    // CREATE
    public boolean addAdminUser(AdminUser user) {
        // For security, this assumes the password hash is already generated.
        String sql = "INSERT INTO admin_user (username, password_hash, role) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getRole());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ ALL
    public List<AdminUser> getAllAdminUsers() {
        List<AdminUser> users = new ArrayList<>();
        String sql = "SELECT id, username, role, created_at FROM admin_user"; // Exclude password_hash
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new AdminUser(
                        rs.getInt("id"),
                        rs.getString("username"),
                        null, // Do not expose password hash
                        rs.getString("role"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // READ ONE by ID
    public AdminUser getAdminUserById(int id) {
        String sql = "SELECT id, username, role, created_at FROM admin_user WHERE id=?"; // Exclude password_hash
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new AdminUser(
                        rs.getInt("id"),
                        rs.getString("username"),
                        null, // Do not expose password hash
                        rs.getString("role"),
                        rs.getTimestamp("created_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // READ ONE by Username (for login - includes hash)
    public AdminUser getAdminUserByUsername(String username) {
        String sql = "SELECT * FROM admin_user WHERE username=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new AdminUser(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getTimestamp("created_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // UPDATE
    public boolean updateAdminUser(AdminUser user) {
        // This method allows updating username and role. Password changes should be handled separately.
        String sql = "UPDATE admin_user SET username=?, role=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getRole());
            stmt.setInt(3, user.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean deleteAdminUser(int id) {
        String sql = "DELETE FROM admin_user WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
