package controller.storefront;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.CustomerDAO;
import dao.OrderDAO;
import model.Customer;
import model.Order;
import model.OrderRequest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.List;

public class StoreOrderController implements HttpHandler {
    private final OrderDAO orderDAO;
    private final CustomerDAO customerDAO;
    private final Gson gson = new Gson();

    public StoreOrderController(Connection connection) {
        this.orderDAO = new OrderDAO(connection);
        this.customerDAO = new CustomerDAO(connection);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response = "";
        int status = 200;

        try {
            if (method.equalsIgnoreCase("GET")) {
                // For now, this can return all orders. In a real app, you'd secure this.
                List<Order> orders = orderDAO.getAllOrders();
                response = gson.toJson(orders);

            } else if (method.equalsIgnoreCase("POST")) {
                // 1. Deserialize request body into OrderRequest DTO
                OrderRequest orderRequest;
                try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody())) {
                    orderRequest = gson.fromJson(reader, OrderRequest.class);
                }

                // 2. Find or create the customer
                Customer customer = customerDAO.getCustomerByEmail(orderRequest.getCustomerEmail());
                if (customer == null) {
                    Customer newCustomer = new Customer();
                    newCustomer.setName(orderRequest.getCustomerName());
                    newCustomer.setEmail(orderRequest.getCustomerEmail());
                    newCustomer.setPhone(orderRequest.getCustomerPhone());
                    newCustomer.setAddress(orderRequest.getCustomerAddress());
                    customer = customerDAO.addCustomer(newCustomer);
                }

                if (customer == null) {
                    status = 500;
                    response = gson.toJson("Failed to create or find customer");
                } else {
                    // 3. Create and save the order
                    Order newOrder = new Order();
                    newOrder.setCustomerId(customer.getId());
                    newOrder.setItems(orderRequest.getItems());
                    newOrder.setTotalAmount(orderRequest.getTotalAmount());
                    newOrder.setStatus("pending");

                    int orderId = orderDAO.addOrder(newOrder);

                    if (orderId != -1) {
                        status = 201;
                        response = gson.toJson(java.util.Map.of("message", "Order created successfully", "orderId", orderId));
                    } else {
                        status = 500;
                        response = gson.toJson("Failed to create order");
                    }
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
