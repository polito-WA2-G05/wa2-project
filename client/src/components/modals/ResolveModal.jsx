// Imports 
import React, {useState} from 'react';
import {Modal, Form, Button} from 'react-bootstrap';

// Styles
import { BsFillPatchCheckFill } from "react-icons/bs"

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
            <Modal.Body className={"my-5"}>
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
                <Button variant="outline-secondary" onClick={handleClose} className="py-2 px-3 rounded-3 fw-semibold">
                    Cancel
                </Button>
                <Button variant="success" onClick={handleSubmit} disabled={!description} className="py-2 px-3 rounded-3 fw-semibold">
                    <BsFillPatchCheckFill size={15} className="me-2" />
                    Resolve ticket
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default ResolveModal