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
                List<Collection> list = dao.getAllCollections();
                response = gson.toJson(list);
            }
            else if (method.equals("POST")) {
                InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
                Collection col = gson.fromJson(reader, Collection.class);
                boolean ok = dao.addCollection(col);
                response = gson.toJson(ok ? "Created" : "Failed");
                status = ok ? 201 : 400;
            }
            else if (method.equals("PUT")) {
                InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
                Collection col = gson.fromJson(reader, Collection.class);
                boolean ok = dao.updateCollection(col);
                response = gson.toJson(ok ? "Updated" : "Failed");
            }
            else if (method.equals("DELETE")) {
                String[] parts = path.split("/");
                int id = Integer.parseInt(parts[parts.length - 1]);
                boolean ok = dao.deleteCollection(id);
                response = gson.toJson(ok ? "Deleted" : "Not Found");
            }
            else {
                response = "Method Not Allowed";
                status = 405;
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
