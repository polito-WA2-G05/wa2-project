// Imports
import React from 'react';
import {Button, Form, Modal} from 'react-bootstrap';

const AttachmentModal = ({show, attachment, onCancel, onAttachmentChange, onConfirm}) => {
    return (
        <Modal show={show} onHide={onCancel} centered>
            <Modal.Header>
                <Modal.Title>Upload attachment</Modal.Title>
            </Modal.Header>
            <Modal.Body className={"my-5"}>
                {attachment && <p><small>Currently selected file:</small> <strong>{attachment.name}</strong></p>}
                <Form.Group>
                    <Form.Control
                        type="file"
                        id="attachment"
                        name="attachment"
                        accept="image/*, .pdf"
                        onChange={onAttachmentChange}
                    />
                </Form.Group>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={onCancel}>
                    Remove attachment
                </Button>
                <Button variant="primary" onClick={onConfirm}>
                    Add attachment
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default AttachmentModal;
