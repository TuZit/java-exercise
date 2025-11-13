import React, { useState, useEffect, useCallback } from "react";
import { Box, Typography, Button, IconButton } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import apiClient from "../api";
import CollectionModal from "../components/CollectionModal";

const emptyCollection = {
  name: "",
  slug: "",
  description: "",
};

export default function Collections() {
  const [collections, setCollections] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCollection, setEditingCollection] = useState(null);

  const fetchCollections = useCallback(async () => {
    try {
      setLoading(true);
      const response = await apiClient.get("/admin/collections");
      setCollections(response.data);
    } catch (error) {
      console.error("Failed to fetch collections:", error);
    }
    setLoading(false);
  }, []);

  useEffect(() => {
    fetchCollections();
  }, [fetchCollections]);

  const handleCreate = () => {
    setEditingCollection(emptyCollection);
    setIsModalOpen(true);
  };

  const handleEdit = (collection) => {
    setEditingCollection(collection);
    setIsModalOpen(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm("Are you sure you want to delete this collection?")) {
      try {
        await apiClient.delete(`/admin/collections/${id}`);
        fetchCollections();
      } catch (error) {
        console.error("Failed to delete collection:", error);
      }
    }
  };

  const handleSave = async () => {
    try {
      if (editingCollection.id) {
        await apiClient.put("/admin/collections", editingCollection);
      } else {
        await apiClient.post("/admin/collections", editingCollection);
      }
      fetchCollections();
    } catch (error) {
      console.error("Failed to save collection:", error);
    }
    setIsModalOpen(false);
    setEditingCollection(null);
  };

  const columns = [
    { field: "id", headerName: "ID", width: 90 },
    { field: "name", headerName: "Name", width: 250 },
    { field: "slug", headerName: "Slug", width: 250 },
    { field: "description", headerName: "Description", width: 400 },
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
        <Typography variant="h4">Collections</Typography>
        <Button variant="contained" onClick={handleCreate}>
          New Collection
        </Button>
      </Box>
      <DataGrid
        rows={collections}
        columns={columns}
        pageSize={10}
        rowsPerPageOptions={[10, 25, 50]}
        loading={loading}
        // checkboxSelection
        // disableSelectionOnClick
        hideFooterSelectedRowCount
        // pageSizeOptions={[10, 25, 50]}
        pagi
      />
      {isModalOpen && (
        <CollectionModal
          show={isModalOpen}
          onHide={() => setIsModalOpen(false)}
          onSave={handleSave}
          collection={editingCollection}
          setCollection={setEditingCollection}
        />
      )}
    </Box>
  );
}
