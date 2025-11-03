import React from 'react';
import { Card } from 'react-bootstrap';
import { Link } from 'react-router-dom';

const ProductCard = ({ product }) => {
  return (
    <Card className="my-3 p-3 rounded product-card">
      <Link to={`/product/${product.id}`}>
        {/* Using a placeholder for the image */}
        <Card.Img src={`https://via.placeholder.com/300x200.png?text=${product.name}`} variant="top" />
      </Link>

      <Card.Body>
        <Link to={`/product/${product.id}`}>
          <Card.Title as="div">
            <strong>{product.name}</strong>
          </Card.Title>
        </Link>

        <Card.Text as="h3">
          ${product.price.toFixed(2)}
        </Card.Text>
      </Card.Body>
    </Card>
  );
};

export default ProductCard;