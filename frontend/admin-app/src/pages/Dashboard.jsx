import React, { useState, useEffect, useCallback } from 'react';
import { Grid, Card, CardContent, Typography, CircularProgress, Box } from '@mui/material';
import apiClient from '../api';

const StatCard = ({ title, value, icon }) => (
  <Card>
    <CardContent>
      <Typography color="textSecondary" gutterBottom>{title}</Typography>
      <Typography variant="h5" component="h2">{value}</Typography>
    </CardContent>
  </Card>
);

export default function Dashboard() {
  const [stats, setStats] = useState({ products: 0, collections: 0, users: 0, orders: 0, revenue: 0 });
  const [loading, setLoading] = useState(true);

  const fetchDashboardData = useCallback(async () => {
    try {
      setLoading(true);
      // Fetch all data in parallel
      const [productsRes, collectionsRes, usersRes, ordersRes] = await Promise.all([
        apiClient.get('/admin/products'),
        apiClient.get('/admin/collections'),
        apiClient.get('/api/admin/users'),
        apiClient.get('/admin/orders'),
      ]);

      const totalRevenue = ordersRes.data.reduce((acc, order) => acc + order.totalAmount, 0);

      setStats({
        products: productsRes.data.length,
        collections: collectionsRes.data.length,
        users: usersRes.data.length,
        orders: ordersRes.data.length,
        revenue: totalRevenue,
      });

    } catch (error) {
      console.error("Failed to fetch dashboard data:", error);
    }
    setLoading(false);
  }, []);

  useEffect(() => {
    fetchDashboardData();
  }, [fetchDashboardData]);

  if (loading) {
    return <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}><CircularProgress /></Box>;
  }

  return (
    <Box>
        <Typography variant="h4" sx={{ mb: 3 }}>
            Shop Dashboard
        </Typography>
        <Grid container spacing={3}>
            <Grid item xs={12} sm={6} md={3}>
                <StatCard title="Total Revenue" value={`$${stats.revenue.toFixed(2)}`} />
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
                <StatCard title="Total Orders" value={stats.orders} />
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
                <StatCard title="Total Products" value={stats.products} />
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
                <StatCard title="Total Users" value={stats.users} />
            </Grid>
        </Grid>
    </Box>
  );
}