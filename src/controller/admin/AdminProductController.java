package controller.admin;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.ProductDAO;
import model.Product;
import java.io.*;
import java.sql.Connection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminProductController implements HttpHandler {
    private final ProductDAO productDAO;
    private final Gson gson = new Gson();
    private static final Pattern productDetailPattern = Pattern.compile("/admin/products/(\\d+)");
    private static final Pattern productsByCollectionPattern = Pattern.compile("/admin/products/collection/(\\d+)");


    public AdminProductController(Connection connection) {
        this.productDAO = new ProductDAO(connection);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String response = "";
        int status = 200;

        try {
            if (method.equals("GET")) {
                Matcher detailMatcher = productDetailPattern.matcher(path);
                Matcher collectionMatcher = productsByCollectionPattern.matcher(path);

                if (detailMatcher.matches()) {
                    int id = Integer.parseInt(detailMatcher.group(1));
                    Product product = productDAO.getProductById(id);
                    if (product != null) {
                        response = gson.toJson(product);
                    } else {
                        status = 404;
                        response = "Product not found";
                    }
                } else if (collectionMatcher.matches()) {
                    int collectionId = Integer.parseInt(collectionMatcher.group(1));
                    List<Product> products = productDAO.getProductsByCollectionId(collectionId);
                    response = gson.toJson(products);
                }
                else {
                    List<Product> list = productDAO.getAllProducts();
                    response = gson.toJson(list);
                }
            }
            else if (method.equals("POST")) {
                InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
                Product product = gson.fromJson(reader, Product.class);
                boolean ok = productDAO.addProduct(product);
                response = gson.toJson(ok ? "Created" : "Failed");
                status = ok ? 201 : 400;
            }
            else if (method.equals("PUT")) {
                InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
                Product product = gson.fromJson(reader, Product.class);
                boolean ok = productDAO.updateProduct(product);
                status = ok ? 200 : 400;
                response = gson.toJson(ok ? "Updated" : "Failed");
            }
            else if (method.equals("DELETE")) {
                String[] parts = path.split("/");
                int id = Integer.parseInt(parts[parts.length - 1]);
                boolean ok = productDAO.deleteProduct(id);
                response = gson.toJson(ok ? "Deleted" : "Not Found");
                status = ok ? 200 : 404;
            }
            else {
                status = 405;
                response = "Method Not Allowed";
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = "Server Error";
            status = 500;
        }

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
