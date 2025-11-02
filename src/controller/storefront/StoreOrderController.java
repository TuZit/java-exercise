package controller.storefront;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.OrderDAO;
import model.Order;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Map;


public class StoreOrderController implements HttpHandler {
    private final OrderDAO orderDAO;
    private final Gson gson = new Gson();

    public StoreOrderController(Connection connection) {
        this.orderDAO = new OrderDAO(connection);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response = "";
        int status = 200;

        try {
            if (method.equalsIgnoreCase("GET")) {
                List<Order> orders = orderDAO.getAllOrders();
                response = gson.toJson(orders);
            } else if (method.equalsIgnoreCase("POST")) {
                Order order = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()), Order.class);
                int orderId = orderDAO.addOrder(order);

                String message = String.format("Updated order %s successfully", orderId);
                response = gson.toJson(Map.of("Updated order success", message));
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
