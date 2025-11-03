import React, { useContext } from 'react';
import { ListGroup, Row, Col, Image, Button, Card, Form } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { CartContext } from '../context/CartContext';

export default function CartPage() {
  const { cart, removeFromCart, updateQuantity } = useContext(CartContext);
  const navigate = useNavigate();

  const subtotal = cart.reduce((acc, item) => acc + item.quantity, 0);
  const totalPrice = cart.reduce((acc, item) => acc + item.quantity * item.price, 0).toFixed(2);

  const checkoutHandler = () => {
    navigate('/checkout'); // We will create this page next
  };

  return (
    <Row>
      <Col md={8}>
        <h1>Shopping Cart</h1>
        {cart.length === 0 ? (
          <p>Your cart is empty. <Link to="/">Go Back</Link></p>
        ) : (
          <ListGroup variant="flush">
            {cart.map(item => (
              <ListGroup.Item key={item.id}>
                <Row className="align-items-center">
                  <Col md={2}>
                    <Image src={`https://via.placeholder.com/100x100.png?text=${item.name}`} alt={item.name} fluid rounded />
                  </Col>
                  <Col md={3}>
                    <Link to={`/product/${item.id}`}>{item.name}</Link>
                  </Col>
                  <Col md={2}>${item.price.toFixed(2)}</Col>
                  <Col md={2}>
                    <Form.Control
                      as="select"
                      value={item.quantity}
                      onChange={(e) => updateQuantity(item.id, Number(e.target.value))}
                    >
                      {[...Array(item.quantity > 10 ? item.quantity : 10).keys()].map(x => (
                        <option key={x + 1} value={x + 1}>{x + 1}</option>
                      ))}
                    </Form.Control>
                  </Col>
                  <Col md={2}>
                    <Button type="button" variant="light" onClick={() => removeFromCart(item.id)}>
                      <i className="fas fa-trash"></i> Remove
                    </Button>
                  </Col>
                </Row>
              </ListGroup.Item>
            ))}
          </ListGroup>
        )}
      </Col>
      <Col md={4}>
        <Card>
          <ListGroup variant="flush">
            <ListGroup.Item>
              <h2>Subtotal ({subtotal}) items</h2>
              ${totalPrice}
            </ListGroup.Item>
            <ListGroup.Item>
              <Button 
                type="button" 
                className="btn-block" 
                disabled={cart.length === 0}
                onClick={checkoutHandler}
              >
                Proceed To Checkout
              </Button>
            </ListGroup.Item>
          </ListGroup>
        </Card>
      </Col>
    </Row>
  );
}