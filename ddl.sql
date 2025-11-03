-- ===========================================================
-- 🛒 DATABASE INIT FOR SIMPLE SHOP SYSTEM (ADMIN + STOREFRONT)
-- ===========================================================

DROP DATABASE IF EXISTS shopdb;
CREATE DATABASE shopdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shopdb;

-- ===========================================================
-- 📦 COLLECTIONS (Bộ sưu tập / danh mục)
-- ===========================================================
CREATE TABLE collection (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    slug VARCHAR(150) UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ===========================================================
-- 🛍️ PRODUCTS (Sản phẩm)
-- ===========================================================
CREATE TABLE product (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    category VARCHAR(100),
    price DOUBLE NOT NULL,
    quantity INT DEFAULT 0,
    description TEXT,
    collection_id INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (collection_id) REFERENCES collection(id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

-- ===========================================================
-- 👤 CUSTOMERS (Khách hàng)
-- ===========================================================
CREATE TABLE customer (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ===========================================================
-- 🧾 ORDERS (Đơn hàng)
-- ===========================================================
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    total_price DOUBLE DEFAULT 0,
    status ENUM('pending', 'paid', 'shipped', 'completed', 'cancelled') DEFAULT 'pending',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE SET NULL
);

-- ===========================================================
-- 📦 ORDER ITEMS (Chi tiết từng sản phẩm trong đơn)
-- ===========================================================
CREATE TABLE order_item (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NULL,
    quantity INT NOT NULL,
    price DOUBLE NOT NULL,
    subtotal DOUBLE GENERATED ALWAYS AS (quantity * price) STORED,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE SET NULL
);

-- ===========================================================
-- 👤 USERS (Tài khoản quản trị CMS)
-- ===========================================================
CREATE TABLE admin_user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('admin', 'editor') DEFAULT 'admin',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ===========================================================
-- 🧪 SAMPLE DATA
-- ===========================================================
INSERT INTO collection (name, description, slug) VALUES
('Electronics', 'Devices and gadgets', 'electronics'),
('Home Appliances', 'Appliances for daily life', 'home-appliances'),
('Fashion', 'Clothes and accessories', 'fashion');

INSERT INTO product (name, category, price, quantity, description, collection_id)
VALUES
('iPhone 14', 'Smartphone', 999.99, 20, 'Latest Apple smartphone', 1),
('Samsung Galaxy S24', 'Smartphone', 899.99, 15, 'Flagship Android phone', 1),
('Dyson Vacuum Cleaner', 'Home', 499.00, 10, 'Powerful home cleaning tool', 2),
('Men T-Shirt', 'Clothing', 25.50, 50, '100% cotton', 3);

INSERT INTO admin_user (username, password_hash, role)
VALUES
('admin', 'admin123', 'admin');

INSERT INTO customer (name, email, phone, address) VALUES
('John Doe', 'john.doe@example.com', '123-456-7890', '123 Main St, Anytown, USA');

-- ===========================================================
-- ✅ DONE
-- ===========================================================
