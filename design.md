project-root/
├── db.properties
├── lib/                    # JDBC driver + gson.jar
├── server/
│   └── Server.java
├── util/
│   └── DatabaseUtil.java
├── model/
│   ├── Product.java
│   ├── Collection.java
│   ├── Inventory.java
│   └── Order.java
├── dao/
│   ├── ProductDAO.java
│   ├── CollectionDAO.java
│   ├── InventoryDAO.java
│   └── OrderDAO.java
└── controller/
    ├── admin/
    │   ├── AdminProductController.java
    │   ├── AdminCollectionController.java
    │   ├── AdminInventoryController.java
    │   └── AdminOrderController.java
    └── storefront/
        ├── StoreProductController.java
        ├── StoreCollectionController.java
        └── StoreOrderController.java
