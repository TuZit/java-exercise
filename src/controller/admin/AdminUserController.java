package controller.admin;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.AdminUserDAO;
import model.AdminUser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.List;

public class AdminUserController implements HttpHandler {
    private final AdminUserDAO dao;
    private final Gson gson = new Gson();

    public AdminUserController(Connection connection) {
        this.dao = new AdminUserDAO(connection);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String response = "";
        int status = 200;

        try {
            String[] parts = path.split("/");
            Integer id = null;
            if (parts.length > 0) {
                try {
                    id = Integer.parseInt(parts[parts.length - 1]);
                } catch (NumberFormatException e) {
                    // It's not an ID, so we ignore it.
                }
            }

            switch (method) {
                case "GET":
                    if (id != null) {
                        AdminUser user = dao.getAdminUserById(id);
                        if (user != null) {
                            response = gson.toJson(user);
                        } else {
                            status = 404;
                            response = gson.toJson("User not found");
                        }
                    } else {
                        List<AdminUser> users = dao.getAllAdminUsers();
                        response = gson.toJson(users);
                    }
                    break;
                case "POST":
                    AdminUser newUser;
                    try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody())) {
                        newUser = gson.fromJson(reader, AdminUser.class);
                    }
                    // In a real app, hash the password here before saving
                    boolean created = dao.addAdminUser(newUser);
                    if (created) {
                        status = 201;
                        response = gson.toJson("User created");
                    } else {
                        status = 400;
                        response = gson.toJson("Failed to create user");
                    }
                    break;
                case "PUT":
                    if (id != null) {
                        AdminUser updatedUser;
                        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody())) {
                            updatedUser = gson.fromJson(reader, AdminUser.class);
                        }
                        updatedUser.setId(id);
                        boolean updated = dao.updateAdminUser(updatedUser);
                        if (updated) {
                            response = gson.toJson("User updated");
                        } else {
                            status = 400;
                            response = gson.toJson("Failed to update user");
                        }
                    } else {
                        status = 400;
                        response = gson.toJson("User ID required for update");
                    }
                    break;
                case "DELETE":
                    if (id != null) {
                        boolean deleted = dao.deleteAdminUser(id);
                        if (deleted) {
                            response = gson.toJson("User deleted");
                        } else {
                            status = 404;
                            response = gson.toJson("User not found");
                        }
                    } else {
                        status = 400;
                        response = gson.toJson("User ID required for delete");
                    }
                    break;
                default:
                    status = 405;
                    response = "Method Not Allowed";
                    break;
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
