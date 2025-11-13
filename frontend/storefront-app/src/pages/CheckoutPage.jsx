import React, { useState, useContext } from 'react';
import { Form, Button, Row, Col, ListGroup, Card } from 'react-bootstrap';
import { useNavigate, useLocation } from 'react-router-dom';
import { CartContext } from '../context/CartContext';
import apiClient from '../api';

export default function CheckoutPage() {
  const { cart: cartFromContext, clearCart } = useContext(CartContext);
  const navigate = useNavigate();
  const location = useLocation();

  // Use the product from location state if available (for direct checkout), otherwise use the cart from context
  const productFromState = location.state?.product;
  const cart = productFromState ? [productFromState] : cartFromContext;
  const isDirectCheckout = !!productFromState;

  const [customer, setCustomer] = useState({
    name: '',
    email: '',
    phone: '',
    address: ''
  });
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  const totalPrice = cart.reduce((acc, item) => acc + item.quantity * item.price, 0).toFixed(2);

  const handleChange = (e) => {
    setCustomer({ ...customer, [e.target.name]: e.target.value });
  };

  const submitHandler = async (e) => {
    e.preventDefault();
    setError(null);

    const orderRequest = {
        customerName: customer.name,
        customerEmail: customer.email,
        customerPhone: customer.phone,
        customerAddress: customer.address,
        totalAmount: totalPrice,
        items: cart.map(item => ({ productId: item.id, quantity: item.quantity, price: item.price }))
    };

    try {
        const { data } = await apiClient.post('/storefront/orders', orderRequest);
        setSuccess(true);
        if (!isDirectCheckout) {
          clearCart();
        }
        // navigate(`/order/${data.orderId}`); // Optional: redirect to an order confirmation page
    } catch (err) {
        setError(err.response?.data?.message || 'An error occurred');
    }
  };

  if (success) {
      return (
          <div className="alert alert-success">
              <h3>Thank you for your order!</h3>
              <p>Your order has been placed successfully.</p>
              <Button onClick={() => navigate('/')}>Continue Shopping</Button>
          </div>
      )
  }

  return (
    <Row>
      <Col md={8}>
        <h2>Shipping Information</h2>
        <Form onSubmit={submitHandler}>
          <Form.Group controlId='name' className="my-3">
            <Form.Label>Name</Form.Label>
            <Form.Control type='text' name='name' required onChange={handleChange}></Form.Control>
          </Form.Group>
          <Form.Group controlId='email' className="my-3">
            <Form.Label>Email Address</Form.Label>
            <Form.Control type='email' name='email' required onChange={handleChange}></Form.Control>
          </Form.Group>
          <Form.Group controlId='phone' className="my-3">
            <Form.Label>Phone Number</Form.Label>
            <Form.Control type='text' name='phone' required onChange={handleChange}></Form.Control>
          </Form.Group>
          <Form.Group controlId='address' className="my-3">
            <Form.Label>Address</Form.Label>
            <Form.Control type='text' name='address' required onChange={handleChange}></Form.Control>
          </Form.Group>
          <Button type='submit' variant='primary'>Place Order</Button>
        </Form>
        {error && <div className="alert alert-danger mt-3">{error}</div>}
      </Col>
      <Col md={4}>
        <Card>
            <ListGroup variant='flush'>
                <ListGroup.Item><h2>Order Summary</h2></ListGroup.Item>
                <ListGroup.Item>
                    <Row>
                        <Col>Items</Col>
                        <Col>${totalPrice}</Col>
                    </Row>
                </ListGroup.Item>
                {/* In a real app, you would add shipping, tax, etc. */}
                <ListGroup.Item>
                    <Row>
                        <Col>Total</Col>
                        <Col>${totalPrice}</Col>
                    </Row>
                </ListGroup.Item>
            </ListGroup>
        </Card>
      </Col>
    </Row>
  );
}
