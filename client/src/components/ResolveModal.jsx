// Imports 
import React, {useState} from 'react';
import {Modal, Form, Button} from 'react-bootstrap';

// Styles
import {BsSend } from "react-icons/bs"

const ResolveModal = ({ show, onHide, onConfirm }) => {
    const [description, setDescription] = useState("");
    
    const handleClose = () => {
        setDescription("");
        onHide();
    };

    const handleSubmit = () => {
        onConfirm(description)
        handleClose();
    };

    return (
        <Modal show={show} onHide={handleClose} centered>
            <Modal.Header closeButton>
                <Modal.Title>Resolve Ticket</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form.Group controlId="description">
                    <Form.Label>Description</Form.Label>
                    <Form.Control
                        as="textarea"
                        rows={4}
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                    />
                </Form.Group>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={handleClose}>
                    Cancel
                </Button>
                <Button variant="primary" onClick={handleSubmit} disabled={!description}>
                    <BsSend size={15} className="me-2" />
                    Submit
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default ResolveModal