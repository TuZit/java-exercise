import React from 'react';
import { Modal, Button, Form } from 'react-bootstrap';

export default function UserModal({ show, onHide, onSave, user, setUser }) {

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUser(prev => ({ ...prev, [name]: value }));
  };

  const isNewUser = !user?.id;

  return (
    <Modal show={show} onHide={onHide}>
      <Modal.Header closeButton>
        <Modal.Title>{isNewUser ? 'Create User' : 'Edit User'}</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form>
          <Form.Group className="mb-3">
            <Form.Label>Username</Form.Label>
            <Form.Control type="text" name="username" value={user?.username || ''} onChange={handleChange} />
          </Form.Group>
          
          {isNewUser && (
            <Form.Group className="mb-3">
              <Form.Label>Password</Form.Label>
              <Form.Control type="password" name="passwordHash" value={user?.passwordHash || ''} onChange={handleChange} />
            </Form.Group>
          )}

          <Form.Group className="mb-3">
            <Form.Label>Role</Form.Label>
            <Form.Control as="select" name="role" value={user?.role || 'admin'} onChange={handleChange}>
                <option value="admin">Admin</option>
                <option value="editor">Editor</option>
            </Form.Control>
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
