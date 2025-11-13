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
    imageURL TEXT,
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

-- INSERT INTO product (name, category, price, quantity, description, collection_id)
-- VALUES
-- ('iPhone 14', 'Smartphone', 999.99, 20, 'Latest Apple smartphone', 1),
-- ('Samsung Galaxy S24', 'Smartphone', 899.99, 15, 'Flagship Android phone', 1),
-- ('Dyson Vacuum Cleaner', 'Home', 499.00, 10, 'Powerful home cleaning tool', 2),
-- ('Men T-Shirt', 'Clothing', 25.50, 50, '100% cotton', 3);
INSERT INTO product (name, category, price, quantity, description, imageURL, created_at, updated_at, collection_id) VALUES ('Apple AirPods 3rd generation with Charging Case', 'gadget accessories, Airbuds', 1700, 8, 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Ex unde illum expedita dolores aut nostrum, quidem placeat laborum nemo, beatae perspiciatis quae, sint tempore aliquid molestiae consequatur eum earum.', 'https://cdn.sanity.io/images/oe6m9gg8/production/9d449761da7e68b17b5fbd5aac5647d40b96903d-500x500.png', '2025-03-05 05:36:30', '2025-11-12 11:24:36', NULL);
INSERT INTO product (name, category, price, quantity, description, imageURL, created_at, updated_at, collection_id) VALUES ('Apple AirPods Max', 'Airbuds', 699, 5, 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Ex unde illum expedita dolores aut nostrum, quidem placeat laborum nemo, beatae perspiciatis quae, sint tempore aliquid molestiae consequatur eum earum.', 'https://cdn.sanity.io/images/oe6m9gg8/production/c3bf5eaa474e9b5220b6839d6808d4a7022ecde6-500x500.webp', '2025-10-26 17:18:34', '2025-11-12 11:24:37', NULL);
INSERT INTO product (name, category, price, quantity, description, imageURL, created_at, updated_at, collection_id) VALUES ('Canon EOS 250D 24.1MP Full HD WI-FI DSLR Camera with 18-55mm', 'gadget accessories, Cameras', 750, 0, 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Ex unde illum expedita dolores aut nostrum, quidem placeat laborum nemo, beatae perspiciatis quae, sint tempore aliquid molestiae consequatur eum earum.', 'https://cdn.sanity.io/images/oe6m9gg8/production/c6e0da93e884e6ab9fc7b2a884c6d28ad7f2020e-500x500.png', '2025-03-05 06:29:38', '2025-11-10 16:09:34', NULL);
INSERT INTO product (name, category, price, quantity, description, imageURL, created_at, updated_at, collection_id) VALUES ('HP Laptop, AMD Ryzen 5 5500U Processor', 'gadget accessories', 1659, 33, 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Ex unde illum expedita dolores aut nostrum, quidem placeat laborum nemo, beatae perspiciatis quae, sint tempore aliquid molestiae consequatur eum earum.', 'https://cdn.sanity.io/images/oe6m9gg8/production/480dae813a99d8331778f7a3edbc6cb98512208c-500x500.png', '2025-02-17 06:36:15', '2025-11-10 16:09:35', NULL);
INSERT INTO product (name, category, price, quantity, description, imageURL, created_at, updated_at, collection_id) VALUES ('Intel 13th Gen Core i9 13900KF Raptor Lake Processor', 'gadget accessories', 799, 15, 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Ex unde illum expedita dolores aut nostrum, quidem placeat laborum nemo, beatae perspiciatis quae, sint tempore aliquid molestiae consequatur eum earum.', 'https://cdn.sanity.io/images/oe6m9gg8/production/b613899aa4e420139f6bf4fb5359e9b8ef8099b1-500x500.webp', '2025-10-26 17:36:09', '2025-11-10 16:09:36', NULL);
INSERT INTO product (name, category, price, quantity, description, imageURL, created_at, updated_at, collection_id) VALUES ('JBL E55BT Wireless Bluetooth Headphone', 'Airbuds', 259, 30, 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Ex unde illum expedita dolores aut nostrum, quidem placeat laborum nemo, beatae perspiciatis quae, sint tempore aliquid molestiae consequatur eum earum.', 'https://cdn.sanity.io/images/oe6m9gg8/production/312a32bc3b5017f870ef43b0af1bc372cd1ba5f2-500x500.jpg', '2025-10-26 17:28:34', '2025-10-26 17:30:24', NULL);
INSERT INTO product (name, category, price, quantity, description, imageURL, created_at, updated_at, collection_id) VALUES ('MacBook Pro M4 Max Chip 16-inch (14-core CPU, 32-core GPU)', 'gadget accessories', 3999, 36, 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Ex unde illum expedita dolores aut nostrum, quidem placeat laborum nemo, beatae perspiciatis quae, sint tempore aliquid molestiae consequatur eum earum.', 'https://cdn.sanity.io/images/oe6m9gg8/production/6c9afa13da86391063f3de593cdce2824d60d450-500x500.webp', '2025-10-26 17:32:01', '2025-11-08 06:53:43', NULL);
INSERT INTO product (name, category, price, quantity, description, imageURL, created_at, updated_at, collection_id) VALUES ('Mpow - CHE2S On-Ear Headphone with Mic for Kids', 'gadget accessories', 550, 62, 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Ex unde illum expedita dolores aut nostrum, quidem placeat laborum nemo, beatae perspiciatis quae, sint tempore aliquid molestiae consequatur eum earum.', 'https://cdn.sanity.io/images/oe6m9gg8/production/38766ca9c29fa255abcfcaa9fd830b60054daf9d-500x500.png', '2025-02-17 09:32:50', '2025-11-08 06:34:39', NULL);
INSERT INTO product (name, category, price, quantity, description, imageURL, created_at, updated_at, collection_id) VALUES ('Realme Note 60x (4/64GB)', 'gadget accessories, Smartphones, Mobiles', 699, 1, 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Ex unde illum expedita dolores aut nostrum, quidem placeat laborum nemo, beatae perspiciatis quae, sint tempore aliquid molestiae consequatur eum earum.', 'https://cdn.sanity.io/images/oe6m9gg8/production/6a182188c90b312b1b3951c1d5b9626ef854b10e-500x500.png', '2025-02-17 09:31:35', '2025-11-05 02:37:14', NULL);
INSERT INTO product (name, category, price, quantity, description, imageURL, created_at, updated_at, collection_id) VALUES ('Samsung Galaxy S25 Ultra 5G 12/256GB', 'gadget accessories, Smartphones', 1659, 99, 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Ex unde illum expedita dolores aut nostrum, quidem placeat laborum nemo, beatae perspiciatis quae, sint tempore aliquid molestiae consequatur eum earum.', 'https://cdn.sanity.io/images/oe6m9gg8/production/a4d5751b9772189b6536fd7720a885cb27be8c6b-500x500.png', '2025-02-17 09:21:53', '2025-11-04 14:47:20', NULL);
INSERT INTO product (name, category, price, quantity, description, imageURL, created_at, updated_at, collection_id) VALUES ('Sony WH-CH520 Wireless Headphones', 'gadget accessories', 499, 30, 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Ex unde illum expedita dolores aut nostrum, quidem placeat laborum nemo, beatae perspiciatis quae, sint tempore aliquid molestiae consequatur eum earum.', 'https://cdn.sanity.io/images/oe6m9gg8/production/c2c857cae8cefda07a357637f3c90b239cd1ae73-500x500.png', '2025-02-17 09:48:42', '2025-10-23 11:32:06', NULL);
INSERT INTO product (name, category, price, quantity, description, imageURL, created_at, updated_at, collection_id) VALUES ('Speak 710 Portable Speaker for Music and Calls', 'gadget accessories', 150, 65, 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Ex unde illum expedita dolores aut nostrum, quidem placeat laborum nemo, beatae perspiciatis quae, sint tempore aliquid molestiae consequatur eum earum.', 'https://cdn.sanity.io/images/oe6m9gg8/production/aa9f2fafc6e277a101e3b17bd4fa00b7d99c6b4b-500x500.png', '2025-02-17 10:09:21', '2025-10-23 11:32:21', NULL);
INSERT INTO product (name, category, price, quantity, description, imageURL, created_at, updated_at, collection_id) VALUES ('Wireless Bluetooth Speaker for Softphones', 'gadget accessories', 220, 59, 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Ex unde illum expedita dolores aut nostrum, quidem placeat laborum nemo, beatae perspiciatis quae, sint tempore aliquid molestiae consequatur eum earum.', 'https://cdn.sanity.io/images/oe6m9gg8/production/bdd6d2597360e46e94a86f7f31018a05d91ac68d-500x500.png', '2025-02-17 10:10:56', '2025-10-23 11:31:45', NULL);
INSERT INTO product (name, category, price, quantity, description, imageURL, created_at, updated_at, collection_id) VALUES ('iPad Pro M4 11-inch (WiFi+Cellular)', 'gadget accessories', 1599, 20, 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Ex unde illum expedita dolores aut nostrum, quidem placeat laborum nemo, beatae perspiciatis quae, sint tempore aliquid molestiae consequatur eum earum.', 'https://cdn.sanity.io/images/oe6m9gg8/production/3724c13132a5fd68eda2479ae549d55bda7447c7-500x500.webp', '2025-10-26 17:33:40', '2025-10-26 17:35:04', NULL);
INSERT INTO product (name, category, price, quantity, description, imageURL, created_at, updated_at, collection_id) VALUES ('iPhone 16 Pro Max 128GB', 'gadget accessories, Smartphones, Mobiles', 1299, 100, 'Lorem ipsum dolor sit amet consectetur adipisicing elit. Ex unde illum expedita dolores aut nostrum, quidem placeat laborum nemo, beatae perspiciatis quae, sint tempore aliquid molestiae consequatur eum earum.', 'https://cdn.sanity.io/images/oe6m9gg8/production/2a75ca8224d0256ac7048b421b82abb95fca0384-500x500.png', '2025-02-17 05:48:04', '2025-10-23 11:31:32', NULL);

INSERT INTO admin_user (username, password_hash, role)
VALUES
('admin', 'admin123', 'admin');

INSERT INTO customer (name, email, phone, address) VALUES
('John Doe', 'john.doe@example.com', '123-456-7890', '123 Main St, Anytown, USA');

-- ===========================================================
-- ✅ DONE
-- ===========================================================
