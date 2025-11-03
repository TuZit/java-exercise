package controller.admin;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.CollectionDAO;
import model.Collection;
import java.io.*;
import java.sql.Connection;
import java.util.List;

public class AdminCollectionController implements HttpHandler {
    private final CollectionDAO dao;
    private final Gson gson = new Gson();

    public AdminCollectionController(Connection connection) {
        this.dao = new CollectionDAO(connection);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String response = "";
        int status = 200;

        try {
            if (method.equals("GET")) {
                String[] parts = path.split("/");
                if (parts.length > 0) {
                    try {
                        int id = Integer.parseInt(parts[parts.length - 1]);
                        Collection collection = dao.getCollectionWithProducts(id);
                        if (collection != null) {
                            response = gson.toJson(collection);
                        } else {
                            status = 404;
                            response = gson.toJson("Collection not found");
                        }
                    } catch (NumberFormatException e) {
                        // Not an ID, so it's a request for all collections
                        List<Collection> list = dao.getAllCollections();
                        response = gson.toJson(list);
                    }
                } else {
                    List<Collection> list = dao.getAllCollections();
                    response = gson.toJson(list);
                }
            }
            else if (method.equals("POST")) {
                Collection col;
                try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody())) {
                    col = gson.fromJson(reader, Collection.class);
                }
                boolean ok = dao.addCollection(col);
                response = gson.toJson(ok ? "Created" : "Failed");
                status = ok ? 201 : 400;
            }
            else if (method.equals("PUT")) {
                Collection col;
                try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody())) {
                    col = gson.fromJson(reader, Collection.class);
                }
                boolean ok = dao.updateCollection(col);
                if (ok) {
                    response = gson.toJson("Updated");
                } else {
                    response = gson.toJson("Failed");
                    status = 400;
                }
            }
            else if (method.equals("DELETE")) {
                String[] parts = path.split("/");
                int id = Integer.parseInt(parts[parts.length - 1]);
                boolean ok = dao.deleteCollection(id);
                if (ok) {
                    response = gson.toJson("Deleted");
                } else {
                    response = gson.toJson("Not Found");
                    status = 404;
                }
            }
            else {
                response = "Method Not Allowed";
                status = 405;
            }
        } catch (NumberFormatException e) {
            status = 400;
            response = gson.toJson("Invalid ID format");
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

