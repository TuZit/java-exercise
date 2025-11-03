import React, { useState, useEffect, useCallback } from 'react';
import { Box, Typography, Button, IconButton } from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import apiClient from '../api';
import UserModal from '../components/UserModal';

const emptyUser = {
  username: '',
  passwordHash: '',
  role: 'editor'
};

export default function Users() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingUser, setEditingUser] = useState(null);

  const fetchUsers = useCallback(async () => {
    try {
      setLoading(true);
      const response = await apiClient.get('/api/admin/users');
      setUsers(response.data);
    } catch (error) {
      console.error("Failed to fetch users:", error);
    }
    setLoading(false);
  }, []);

  useEffect(() => {
    fetchUsers();
  }, [fetchUsers]);

  const handleCreate = () => {
    setEditingUser(emptyUser);
    setIsModalOpen(true);
  };

  const handleEdit = (user) => {
    setEditingUser(user);
    setIsModalOpen(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this user?')) {
      try {
        await apiClient.delete(`/api/admin/users/${id}`);
        fetchUsers();
      } catch (error) {
        console.error("Failed to delete user:", error);
      }
    }
  };

  const handleSave = async () => {
    try {
      if (editingUser.id) {
        await apiClient.put(`/api/admin/users/${editingUser.id}`, editingUser);
      } else {
        await apiClient.post('/api/admin/users', editingUser);
      }
      fetchUsers();
    } catch (error) {
      console.error("Failed to save user:", error);
    }
    setIsModalOpen(false);
    setEditingUser(null);
  };

  const columns = [
    { field: 'id', headerName: 'ID', width: 90 },
    { field: 'username', headerName: 'Username', width: 250 },
    { field: 'role', headerName: 'Role', width: 150 },
    {
        field: 'actions',
        headerName: 'Actions',
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
    }
  ];

  return (
    <Box sx={{ height: '80vh', width: '100%' }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <Typography variant="h4">
          Users
        </Typography>
        <Button variant="contained" onClick={handleCreate}>New User</Button>
      </Box>
      <DataGrid
        rows={users}
        columns={columns}
        pageSize={10}
        rowsPerPageOptions={[10]}
        loading={loading}
        checkboxSelection
        disableSelectionOnClick
      />
      {isModalOpen && (
        <UserModal 
            show={isModalOpen} 
            onHide={() => setIsModalOpen(false)} 
            onSave={handleSave} 
            user={editingUser}
            setUser={setEditingUser}
        />
      )}
    </Box>
  );
}