package dao;

import model.Order;
import model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private Connection connection;

    public OrderDAO(Connection connection) {
        this.connection = connection;
    }

    // ======================
    // 🟢 CREATE (Add Order)
    // ======================
    public int addOrder(Order order) {
        String orderSql = "INSERT INTO orders (customer_name, customer_email, customer_phone, customer_address, total_price, status, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())";

        String orderItemSql = "INSERT INTO order_item (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        int orderId = -1;

        try {
            // 🚀 Bắt đầu transaction
            connection.setAutoCommit(false);

            // 1️⃣ Insert vào bảng orders
            try (PreparedStatement orderStmt = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                orderStmt.setString(1, order.getCustomerName());
                orderStmt.setString(2, order.getCustomerEmail());
                orderStmt.setString(3, order.getCustomerPhone());
//                orderStmt.setString(4, order.getCustomerAddress());
                orderStmt.setDouble(5, order.getTotalAmount());
                orderStmt.setString(6, order.getStatus());
                orderStmt.executeUpdate();

                // Lấy ID order vừa tạo
                try (ResultSet rs = orderStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    }
                }
            }

            // 2️⃣ Insert danh sách order_item
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                try (PreparedStatement itemStmt = connection.prepareStatement(orderItemSql)) {
                    for (OrderItem item : order.getItems()) {
                        itemStmt.setInt(1, orderId);
                        itemStmt.setInt(2, item.getProductId());
                        itemStmt.setInt(3, item.getQuantity());
                        itemStmt.setDouble(4, item.getPrice());
                        itemStmt.addBatch();
                    }
                    itemStmt.executeBatch();
                }
            }

            // 3️⃣ Commit transaction
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            orderId = -1;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return orderId;
    }

    // ======================
    // 🔵 READ ALL Orders
    // ======================
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY created_at DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("id"),
                        rs.getString("customer_name"),
                        rs.getString("customer_email"),
                        rs.getString("customer_phone"),
                        rs.getDouble("total_amount"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                );

                // Load order items
                order.setItems(getOrderItemsByOrderId(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    // ======================
    // 🔍 READ One Order
    // ======================
    public Order getOrderById(int id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Order order = new Order(
                        rs.getInt("id"),
                        rs.getString("customer_name"),
                        rs.getString("customer_email"),
                        rs.getString("customer_phone"),
                        rs.getDouble("total_amount"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                );
                order.setItems(getOrderItemsByOrderId(id));
                return order;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ======================
    // 🔸 Helper - Get OrderItems
    // ======================
    private List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                items.add(new OrderItem(
                        rs.getInt("id"),
                        rs.getInt("order_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    // ======================
    // 🟡 UPDATE (Status)
    // ======================
    public boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ?, updated_at = NOW() WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ======================
    // 🔴 DELETE
    // ======================
    public boolean deleteOrder(int orderId) {
        String deleteItems = "DELETE FROM order_items WHERE order_id = ?";
        String deleteOrder = "DELETE FROM orders WHERE id = ?";
        try {
            connection.setAutoCommit(false);

            PreparedStatement stmtItems = connection.prepareStatement(deleteItems);
            stmtItems.setInt(1, orderId);
            stmtItems.executeUpdate();

            PreparedStatement stmtOrder = connection.prepareStatement(deleteOrder);
            stmtOrder.setInt(1, orderId);
            stmtOrder.executeUpdate();

            connection.commit();
            connection.setAutoCommit(true);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }
}
