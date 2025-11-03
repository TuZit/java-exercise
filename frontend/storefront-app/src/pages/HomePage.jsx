import React, { useState, useEffect } from 'react';
import { Row, Col } from 'react-bootstrap';
import apiClient from '../api';
import ProductCard from '../components/ProductCard';

import Hero from '../components/Hero';

export default function HomePage() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        setLoading(true);
        const { data } = await apiClient.get('/store/products');
        setProducts(data);
      } catch (error) {
        console.error("Failed to fetch products:", error);
      }
      setLoading(false);
    };

    fetchProducts();
  }, []);

  return (
    <>
      <Hero />
      <h1>Latest Products</h1>
      {loading ? (
        <h2>Loading...</h2>
      ) : (
        <Row>
          {products.map((product) => (
            <Col key={product.id} sm={12} md={6} lg={4} xl={3}>
              <ProductCard product={product} />
            </Col>
          ))}
        </Row>
      )}
    </>
  );
}
