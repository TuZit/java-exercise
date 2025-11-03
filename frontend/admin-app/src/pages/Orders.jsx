import React, { useState, useEffect, useCallback } from 'react';
import { Box, Typography, IconButton } from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import VisibilityIcon from '@mui/icons-material/Visibility';
import apiClient from '../api';
import OrderModal from '../components/OrderModal';

export default function Orders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState(null);

  const fetchOrders = useCallback(async () => {
    try {
      setLoading(true);
      const response = await apiClient.get('/admin/orders');
      setOrders(response.data);
    } catch (error) {
      console.error("Failed to fetch orders:", error);
    }
    setLoading(false);
  }, []);

  useEffect(() => {
    fetchOrders();
  }, [fetchOrders]);

  const handleView = async (id) => {
    try {
        const response = await apiClient.get(`/admin/orders/${id}`);
        setSelectedOrder(response.data);
        setIsModalOpen(true);
    } catch (error) {
        console.error("Failed to fetch order details:", error);
    }
  };

  const handleSave = async () => {
    if (!selectedOrder) return;
    try {
      await apiClient.put(`/admin/orders/${selectedOrder.id}`, { status: selectedOrder.status });
      fetchOrders(); // Refetch to show updated status in the grid
    } catch (error) {
      console.error("Failed to update order status:", error);
    }
    setIsModalOpen(false);
    setSelectedOrder(null);
  };

  const columns = [
    { field: 'id', headerName: 'Order ID', width: 100 },
    {
        field: 'customerName',
        headerName: 'Customer',
        width: 200,
        valueGetter: (params) => params.row.customer?.name || 'N/A',
    },
    {
        field: 'customerEmail',
        headerName: 'Email',
        width: 250,
        valueGetter: (params) => params.row.customer?.email || 'N/A',
    },
    {
        field: 'totalAmount',
        headerName: 'Total',
        type: 'number',
        width: 120,
        valueFormatter: (params) => `$${params.value.toFixed(2)}`,
    },
    { field: 'status', headerName: 'Status', width: 130 },
    {
        field: 'createdAt',
        headerName: 'Date',
        width: 200,
        valueFormatter: (params) => new Date(params.value).toLocaleString(),
    },
    {
        field: 'actions',
        headerName: 'Actions',
        sortable: false,
        width: 100,
        renderCell: (params) => (
            <IconButton onClick={() => handleView(params.row.id)}>
                <VisibilityIcon />
            </IconButton>
        ),
    }
  ];

  return (
    <Box sx={{ height: '80vh', width: '100%' }}>
      <Typography variant="h4" sx={{ mb: 2 }}>
        Orders
      </Typography>
      <DataGrid
        rows={orders}
        columns={columns}
        pageSize={10}
        rowsPerPageOptions={[10]}
        loading={loading}
        disableSelectionOnClick
      />
      {isModalOpen && selectedOrder && (
        <OrderModal 
            show={isModalOpen} 
            onHide={() => setIsModalOpen(false)} 
            onSave={handleSave} 
            order={selectedOrder}
            setOrder={setSelectedOrder}
        />
      )}
    </Box>
  );
}