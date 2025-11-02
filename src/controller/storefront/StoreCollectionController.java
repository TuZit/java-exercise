package controller.storefront;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.OrderDAO;
import dao.OrderItemDAO;
import model.Order;
import model.OrderItem;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Map;

public class StoreCollectionController implements HttpHandler {
    private final OrderDAO orderDAO;
    private final OrderItemDAO orderItemDAO;
    private final Gson gson = new Gson();

    public StoreCollectionController(Connection connection) {
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
            if (method.equalsIgnoreCase("POST")) {
                // 🧾 Tạo order mới
                Order newOrder = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), Order.class);
                int orderId = orderDAO.addOrder(newOrder);
                if (orderId > 0 && newOrder.getItems() != null) {
                    for (OrderItem item : newOrder.getItems()) {
                        item.setOrderId(orderId);
                        orderItemDAO.addOrderItem(item);
                    }
                }
                response = gson.toJson(Map.of("success", true, "order_id", orderId));

            } else if (method.equalsIgnoreCase("GET")) {
                String[] parts = path.split("/");
                int id = Integer.parseInt(parts[parts.length - 1]);

               Order order = orderDAO.getOrderById(id);
                response = gson.toJson(order);

//                if (id) {
//                    List<Order> orders = orderDAO.getOrderById(id);
//                    for (Order o : orders) {
//                        o.setItems(orderItemDAO.getItemsByOrderId(o.getId()));
//                    }
//                    response = gson.toJson(orders);
//                } else {
//                    status = 400;
//                    response = "Missing email parameter";
//                }

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

    // private Map<String, String> parseQuery(String query) {
    //     if (query == null || query.isEmpty()) return Map.of();
    //     return Arrays.stream(query.split("&"))
    //             .map(p -> p.split("="))
    //             .filter(arr -> arr.length == 2)
    //             .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
    // }
}
