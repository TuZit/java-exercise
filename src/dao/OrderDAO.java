package dao;

import model.Customer;
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

    public int addOrder(Order order) {
        String orderSql = "INSERT INTO orders (customer_id, total_price, status) VALUES (?, ?, ?)";
        String orderItemSql = "INSERT INTO order_item (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        int orderId = -1;

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement orderStmt = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                orderStmt.setInt(1, order.getCustomerId());
                orderStmt.setDouble(2, order.getTotalAmount());
                orderStmt.setString(3, order.getStatus());
                orderStmt.executeUpdate();

                try (ResultSet rs = orderStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    }
                }
            }

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

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, c.name as customer_name, c.email as customer_email, c.phone as customer_phone, c.address as customer_address " +
                     "FROM orders o JOIN customer c ON o.customer_id = c.id ORDER BY o.created_at DESC";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Order order = mapRowToOrderWithCustomer(rs);
                order.setItems(getOrderItemsByOrderId(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public Order getOrderById(int id) {
        String sql = "SELECT o.*, c.name as customer_name, c.email as customer_email, c.phone as customer_phone, c.address as customer_address " +
                     "FROM orders o JOIN customer c ON o.customer_id = c.id WHERE o.id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order order = mapRowToOrderWithCustomer(rs);
                    order.setItems(getOrderItemsByOrderId(id));
                    return order;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_item WHERE order_id = ?";
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

    public boolean deleteOrder(int orderId) {
        String deleteItems = "DELETE FROM order_item WHERE order_id = ?";
        String deleteOrder = "DELETE FROM orders WHERE id = ?";
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmtItems = connection.prepareStatement(deleteItems)) {
                stmtItems.setInt(1, orderId);
                stmtItems.executeUpdate();
            }

            try (PreparedStatement stmtOrder = connection.prepareStatement(deleteOrder)) {
                stmtOrder.setInt(1, orderId);
                stmtOrder.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private Order mapRowToOrderWithCustomer(ResultSet rs) throws SQLException {
        Order order = new Order(
                rs.getInt("id"),
                rs.getInt("customer_id"),
                rs.getDouble("total_price"),
                rs.getString("status"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at")
        );
        Customer customer = new Customer(
                rs.getInt("customer_id"),
                rs.getString("customer_name"),
                rs.getString("customer_email"),
                rs.getString("customer_phone"),
                rs.getString("customer_address"),
                null // createdAt for customer is not fetched here
        );
        order.setCustomer(customer);
        return order;
    }
}
