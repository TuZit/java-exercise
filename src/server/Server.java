package server;

import com.sun.net.httpserver.HttpServer;

import util.DatabaseUtil;
import controller.admin.*;
import controller.storefront.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;

public class Server {
    public static void main(String[] args) throws IOException {
        Connection connection = DatabaseUtil.getConnection();
        if (connection == null) {
            System.out.println("❌ Cannot connect to database!");
            return;
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        CorsFilter corsFilter = new CorsFilter();

        // 🔧 Admin APIs
        server.createContext("/admin/products", new AdminProductController(connection)).getFilters().add(corsFilter);
        server.createContext("/admin/collections", new AdminCollectionController(connection)).getFilters().add(corsFilter);
        server.createContext("/admin/orders", new AdminOrderController(connection)).getFilters().add(corsFilter);
        server.createContext("/api/admin/users", new AdminUserController(connection)).getFilters().add(corsFilter);

        // 🛍️ Storefront APIs
        server.createContext("/store/products", new StoreProductController(connection)).getFilters().add(corsFilter);
        server.createContext("/store/collections", new StoreCollectionController(connection)).getFilters().add(corsFilter);
        server.createContext("/storefront/orders", new StoreOrderController(connection)).getFilters().add(corsFilter);

        server.start();
        System.out.println("✅ Server running on http://localhost:8080");
    }
}
