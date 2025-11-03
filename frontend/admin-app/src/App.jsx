import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import Dashboard from './pages/Dashboard';
import Products from './pages/Products';
import Collections from './pages/Collections';
import Orders from './pages/Orders';
import Users from './pages/Users';

function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/products" element={<Products />} />
        <Route path="/collections" element={<Collections />} />
        <Route path="/orders" element={<Orders />} />
        <Route path="/users" element={<Users />} />
      </Routes>
    </Layout>
  );
}

export default App;