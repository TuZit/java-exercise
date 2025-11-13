import React, { useState, useEffect } from "react";
import { Modal, Button, Form } from "react-bootstrap";
import apiClient from "../api";

export default function ProductModal({
  show,
  onHide,
  onSave,
  product,
  setProduct,
}) {
  const [collections, setCollections] = useState([]);

  useEffect(() => {
    if (show) {
      apiClient
        .get("/admin/collections")
        .then((response) => {
          setCollections(response.data);
        })
        .catch((error) => {
          console.error("Failed to fetch collections:", error);
        });
    }
  }, [show]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setProduct((prev) => ({ ...prev, [name]: value }));
  };

  return (
    <Modal show={show} onHide={onHide}>
      <Modal.Header closeButton>
        <Modal.Title>
          {product && product.id ? "Edit Product" : "Create Product"}
        </Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form>
          <Form.Group className="mb-3">
            <Form.Label>Name</Form.Label>
            <Form.Control
              type="text"
              name="name"
              value={product?.name || ""}
              onChange={handleChange}
            />
          </Form.Group>
          {/* <Form.Group className="mb-3">
            <Form.Label>Category</Form.Label>
            <Form.Control type="text" name="category" value={product?.category || ''} onChange={handleChange} />
          </Form.Group> */}
          <Form.Group className="mb-3">
            <Form.Label>Collection</Form.Label>
            <Form.Select
              name="collectionId"
              value={product?.collectionId || ""}
              onChange={handleChange}>
              <option value="">Select a collection</option>
              {collections.map((collection) => (
                <option key={collection.id} value={collection.id}>
                  {collection.name}
                </option>
              ))}
            </Form.Select>
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Price</Form.Label>
            <Form.Control
              type="number"
              name="price"
              value={product?.price || 0}
              onChange={handleChange}
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Quantity</Form.Label>
            <Form.Control
              type="number"
              name="quantity"
              value={product?.quantity || 0}
              onChange={handleChange}
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Description</Form.Label>
            <Form.Control
              as="textarea"
              rows={3}
              name="description"
              value={product?.description || ""}
              onChange={handleChange}
            />
          </Form.Group>
        </Form>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onHide}>
          Close
        </Button>
        <Button variant="primary" onClick={onSave}>
          Save Changes
        </Button>
      </Modal.Footer>
    </Modal>
  );
}
