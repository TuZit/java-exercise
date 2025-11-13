import React, { useState, useEffect, useCallback } from "react";
import { Box, Typography, Button, IconButton } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import apiClient from "../api";
import ProductModal from "../components/ProductModal";

const emptyProduct = {
  name: "",
  category: "",
  price: 0,
  quantity: 0,
  description: "",
  collectionId: "",
};

export default function Products() {
  const [products, setProducts] = useState([]);
  const [collections, setCollections] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);

  const collectionMap = React.useMemo(() => {
    return collections.reduce((map, collection) => {
      map[collection.id] = collection.name;
      return map;
    }, {});
  }, [collections]);

  const fetchProducts = useCallback(async () => {
    try {
      setLoading(true);
      const [productsResponse, collectionsResponse] = await Promise.all([
        apiClient.get("/admin/products"),
        apiClient.get("/admin/collections"),
      ]);
      setProducts(productsResponse.data);
      setCollections(collectionsResponse.data);
    } catch (error) {
      console.error("Failed to fetch data:", error);
    }
    setLoading(false);
  }, []);

  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  const handleCreate = () => {
    setEditingProduct(emptyProduct);
    setIsModalOpen(true);
  };

  const handleEdit = (product) => {
    setEditingProduct(product);
    setIsModalOpen(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm("Are you sure you want to delete this product?")) {
      try {
        await apiClient.delete(`/admin/products/${id}`);
        fetchProducts(); // Refetch after delete
      } catch (error) {
        console.error("Failed to delete product:", error);
      }
    }
  };

  const handleSave = async () => {
    try {
      if (editingProduct.id) {
        // Update existing product
        await apiClient.put("/admin/products", editingProduct);
      } else {
        // Create new product
        await apiClient.post("/admin/products", editingProduct);
      }
      fetchProducts(); // Refetch after save
    } catch (error) {
      console.error("Failed to save product:", error);
    }
    setIsModalOpen(false);
    setEditingProduct(null);
  };

  const columns = [
    { field: "id", headerName: "ID", width: 90 },
    { field: "name", headerName: "Name", width: 250 },
    // { field: "category", headerName: "Category", width: 150 },
    {
      field: "price",
      headerName: "Price",
      type: "number",
      width: 110,
      valueFormatter: (_, params) => `${params?.price?.toFixed(2)}`,
    },
    {
      field: "quantity",
      headerName: "Quantity",
      type: "number",
      width: 110,
    },
    {
      field: "collectionId",
      headerName: "Collection",
      width: 150,
      valueGetter: (_, params) => collectionMap[params.collectionId] || "N/A",
    },
    {
      field: "actions",
      headerName: "Actions",
      sortable: false,
      width: 120,
      renderCell: (params) => (
        <Box>
          <IconButton onClick={() => handleEdit(params.row)}>
            <EditIcon />
          </IconButton>
          <IconButton onClick={() => handleDelete(params.row.id)}>
            <DeleteIcon />
          </IconButton>
        </Box>
      ),
    },
  ];

  return (
    <Box sx={{ height: "80vh", width: "100%" }}>
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          mb: 2,
        }}>
        <Typography variant="h4">Products</Typography>
        <Button variant="contained" onClick={handleCreate}>
          New Product
        </Button>
      </Box>
      <DataGrid
        rows={products}
        columns={columns}
        pageSize={10}
        rowsPerPageOptions={[10]}
        loading={loading}
        // checkboxSelection
        // disableSelectionOnClick
      />
      {isModalOpen && (
        <ProductModal
          show={isModalOpen}
          onHide={() => setIsModalOpen(false)}
          onSave={handleSave}
          product={editingProduct}
          setProduct={setEditingProduct}
        />
      )}
    </Box>
  );
}
