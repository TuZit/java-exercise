package controller.storefront;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.ProductDAO;
import model.Product;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;

public class StoreProductController implements HttpHandler {
    private final ProductDAO productDAO;
    private final Gson gson = new Gson();

    public StoreProductController(Connection connection) {
        this.productDAO = new ProductDAO(connection);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response;
        int status = 200;

        try {
            URI uri = exchange.getRequestURI();
            String path = uri.getPath(); // e.g., /store/products or /store/products/123
            String[] pathSegments = path.substring("/store/products".length()).split("/");

            if (pathSegments.length > 1 && !pathSegments[1].isEmpty()) {
                // This means we have something like /store/products/123
                try {
                    int id = Integer.parseInt(pathSegments[1]);
                    Product product = productDAO.getProductById(id);
                    if (product != null) {
                        response = gson.toJson(product);
                    } else {
                        status = 404;
                        response = "Product not found";
                    }
                } catch (NumberFormatException e) {
                    status = 400;
                    response = "Invalid product ID";
                }
            } else {
                // This means we have /store/products
                Map<String, String> query = parseQuery(uri.getQuery());
                String collectionIdStr = query.get("collection_id");

                List<Product> products = productDAO.getAllProducts();

                if (collectionIdStr != null) {
                    try {
                        int collectionId = Integer.parseInt(collectionIdStr);
                        products = products.stream()
                                .filter(p -> p.getCollectionId() == collectionId)
                                .collect(Collectors.toList());
                    } catch (NumberFormatException e) {
                        // Ignore if collection_id is not a valid number
                    }
                }

                response = gson.toJson(products);
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = 500;
            response = "Server Error";
        }

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private Map<String, String> parseQuery(String query) {
        if (query == null || query.isEmpty()) return Map.of();
        return Arrays.stream(query.split("&"))
                .map(p -> p.split("="))
                .filter(arr -> arr.length == 2)
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
    }
}
