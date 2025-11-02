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

        // 🔧 Admin APIs
        server.createContext("/admin/products", new AdminProductController(connection));
        server.createContext("/admin/collections", new AdminCollectionController(connection));
        server.createContext("/admin/orders", new AdminOrderController(connection));

        // 🛍️ Storefront APIs
        server.createContext("/store/products", new StoreProductController(connection));
        server.createContext("/store/collections", new StoreCollectionController(connection));
        server.createContext("/storefront/orders", new StoreOrderController(connection));

        server.start();
        System.out.println("✅ Server running on http://localhost:8080");
    }
}
