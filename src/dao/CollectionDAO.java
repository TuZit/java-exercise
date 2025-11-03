package dao;

import model.Collection;
import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CollectionDAO {
    private Connection connection;

    public CollectionDAO(Connection connection) {
        this.connection = connection;
    }

    // CREATE
    public boolean addCollection(Collection col) {
        String sql = "INSERT INTO collection (name, description, slug, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, col.getName());
            stmt.setString(2, col.getDescription());
            stmt.setString(3, col.getSlug());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ ALL
    public List<Collection> getAllCollections() {
        List<Collection> list = new ArrayList<>();
        String sql = "SELECT * FROM collection";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Collection(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("slug"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // READ ONE
    public Collection getCollectionById(int id) {
        String sql = "SELECT * FROM collection WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Collection(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("slug"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // READ ONE WITH PRODUCTS
    public Collection getCollectionWithProducts(int id) {
        Collection collection = getCollectionById(id);
        if (collection != null) {
            ProductDAO productDAO = new ProductDAO(this.connection);
            List<Product> products = productDAO.getProductsByCollectionId(id);
            collection.setProducts(products);
        }
        return collection;
    }

    // UPDATE
    public boolean updateCollection(Collection col) {
        String sql = "UPDATE collection SET name=?, description=?, slug=?, updated_at=NOW() WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, col.getName());
            stmt.setString(2, col.getDescription());
            stmt.setString(3, col.getSlug());
            stmt.setInt(4, col.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean deleteCollection(int id) {
        String sql = "DELETE FROM collection WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
