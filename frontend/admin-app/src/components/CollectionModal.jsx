import React from 'react';
import { Modal, Button, Form } from 'react-bootstrap';

export default function CollectionModal({ show, onHide, onSave, collection, setCollection }) {

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCollection(prev => ({ ...prev, [name]: value }));
  };

  return (
    <Modal show={show} onHide={onHide}>
      <Modal.Header closeButton>
        <Modal.Title>{collection && collection.id ? 'Edit Collection' : 'Create Collection'}</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form>
          <Form.Group className="mb-3">
            <Form.Label>Name</Form.Label>
            <Form.Control type="text" name="name" value={collection?.name || ''} onChange={handleChange} />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Slug</Form.Label>
            <Form.Control type="text" name="slug" value={collection?.slug || ''} onChange={handleChange} />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Description</Form.Label>
            <Form.Control as="textarea" rows={3} name="description" value={collection?.description || ''} onChange={handleChange} />
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
