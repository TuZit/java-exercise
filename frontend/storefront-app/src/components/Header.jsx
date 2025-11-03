import React, { useContext } from 'react';
import { Navbar, Nav, Container, Badge } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import { CartContext } from '../context/CartContext';

export default function Header() {
  const { cart } = useContext(CartContext);
  const itemCount = cart.reduce((sum, item) => sum + item.quantity, 0);

  return (
    <Navbar bg="white" variant="light" expand="lg" collapseOnSelect className="mb-4">
      <Container>
        <LinkContainer to="/">
          <Navbar.Brand><strong>React-Shop</strong></Navbar.Brand>
        </LinkContainer>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="ms-auto">
            <LinkContainer to="/cart">
              <Nav.Link>
                <i className="fas fa-shopping-cart"></i> Cart 
                {itemCount > 0 && <Badge pill bg="primary" className="ms-1">{itemCount}</Badge>}
              </Nav.Link>
            </LinkContainer>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}