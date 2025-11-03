import React from 'react';
import { Container, Button } from 'react-bootstrap';
import { Link } from 'react-router-dom';

const Hero = () => {
  return (
    <div className="hero-section">
      <Container fluid className="py-5">
        <h1 className="display-4 fw-bold">Discover Your Next Favorite Item</h1>
        <p className="col-md-8 fs-5 mx-auto">Explore our curated collection of high-quality products, designed to elevate your everyday.</p>
        <Button variant="light" size="lg" as={Link} to="/products">
          Shop Now
        </Button>
      </Container>
    </div>
  );
};

export default Hero;