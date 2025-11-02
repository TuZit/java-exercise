package controller.admin;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.OrderDAO;
import dao.OrderItemDAO;
import model.Order;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.List;

public class AdminOrderController implements HttpHandler {
    private final OrderDAO orderDAO;
    private final OrderItemDAO orderItemDAO;
    private final Gson gson = new Gson();

    public AdminOrderController(Connection connection) {
        this.orderDAO = new OrderDAO(connection);
        this.orderItemDAO = new OrderItemDAO(connection);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String response = "";
        int status = 200;

        try {
            if (method.equalsIgnoreCase("GET")) {
                // GET /admin/orders or /admin/orders/{id}
                String[] parts = path.split("/");
                if (parts.length == 4) {
                    int orderId = Integer.parseInt(parts[3]);
                    Order order = orderDAO.getOrderById(orderId);
                    if (order != null) {
                        order.setItems(orderItemDAO.getItemsByOrderId(orderId));
                        response = gson.toJson(order);
                    } else {
                        status = 404;
                        response = "Order not found";
                    }
                } else {
                    List<Order> orders = orderDAO.getAllOrders();
                    response = gson.toJson(orders);
                }

            } else if (method.equalsIgnoreCase("PUT")) {
                // PUT /admin/orders/{id}
                String[] parts = path.split("/");
                if (parts.length == 4) {
                    int orderId = Integer.parseInt(parts[3]);
                    Order updatedOrder = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), Order.class);
                    boolean success = orderDAO.updateOrderStatus(orderId, updatedOrder.getStatus());
                    response = gson.toJson(success ? "Order updated" : "Update failed");
                } else {
                    status = 400;
                    response = "Invalid request path";
                }

            } else {
                status = 405;
                response = "Method Not Allowed";
            }

        } catch (Exception e) {
            e.printStackTrace();
            status = 500;
            response = "Server Error";
        }

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
