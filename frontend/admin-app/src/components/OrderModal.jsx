import React from 'react';
import { Modal, Button, Form, ListGroup, Badge } from 'react-bootstrap';

export default function OrderModal({ show, onHide, onSave, order, setOrder }) {

  const handleStatusChange = (e) => {
    setOrder(prev => ({ ...prev, status: e.target.value }));
  };

  return (
    <Modal show={show} onHide={onHide} size="lg">
      <Modal.Header closeButton>
        <Modal.Title>Order Details #{order?.id}</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        {order ? (
          <div>
            <h5>Customer Details</h5>
            <p><strong>Name:</strong> {order.customer?.name}</p>
            <p><strong>Email:</strong> {order.customer?.email}</p>
            
            <hr />

            <h5>Order Items</h5>
            <ListGroup variant="flush">
              {order.items?.map(item => (
                <ListGroup.Item key={item.id} className="d-flex justify-content-between align-items-start">
                  <div className="ms-2 me-auto">
                    <div className="fw-bold">Product ID: {item.productId}</div>
                    Price: ${item?.price?.toFixed(2)}
                  </div>
                  <Badge bg="primary" pill>
                    Quantity: {item?.quantity}
                  </Badge>
                </ListGroup.Item>
              ))}
            </ListGroup>

            <hr />

            <h5>Status</h5>
            <Form.Group>
              <Form.Label>Update Order Status</Form.Label>
              <Form.Control as="select" value={order?.status} onChange={handleStatusChange}>
                <option value="pending">Pending</option>
                <option value="paid">Paid</option>
                <option value="shipped">Shipped</option>
                <option value="completed">Completed</option>
                <option value="cancelled">Cancelled</option>
              </Form.Control>
            </Form.Group>

          </div>
        ) : (
          <p>Loading...</p>
        )}
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onHide}>
          Close
        </Button>
        <Button variant="primary" onClick={onSave}>
          Save Status
        </Button>
      </Modal.Footer>
    </Modal>
  );
}
