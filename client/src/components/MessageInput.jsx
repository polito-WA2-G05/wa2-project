// Imports
import React, { useContext, useState } from 'react';
import { Button, Form } from 'react-bootstrap';

// Components
import { AttachmentModal } from '@components'

import { SessionContext } from "@contexts"
import api from "@services"

// Styles
import { BsSend } from "react-icons/bs";
import { ImAttachment } from "react-icons/im"

const MessageInput = ({ ticket, sendMessage }) => {
    const [message, setMessage] = useState('');
    const [showAttachmentModal, setShowAttachmentModal] = useState(false);
    const [attachment, setAttachment] = useState(null);

    const { role, onError } = useContext(SessionContext);

    const handleInsert = (e) => {
        setMessage(e.target.value);
    };

    const handleAttachmentChange = (e) => {
        const file = e.target.files[0];
        setAttachment(file)
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (message.length > 0) {
            sendMessage(message)
            setMessage("");
            setAttachment(null);
        }
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSubmit(e);
        }
    };

    const handleUploadFile = () => {
        setShowAttachmentModal(false)
        let formData = new FormData()
        formData.append("file", attachment)

        api.utils.uploadAttachment(role, ticket.id, formData)
            .then((data) => console.log(data))
            .catch(err => onError(err.detail ?? err))
            .finally(() => setAttachment(null))
    }

    return (
        <>
            <Form onSubmit={handleSubmit} className="mt-4 d-flex flex-row justify-content-between align-items-center">
                <Form.Control
                    id="message"
                    as="textarea"
                    placeholder="Type something..."
                    value={message}
                    onChange={handleInsert}
                    onKeyPress={handleKeyPress}
                />
                <Button id="attachment" onClick={() => setShowAttachmentModal(true)} className="p-3 mx-4">
                    <ImAttachment size={20} />
                </Button>
                <Button id="message" type="submit" className="p-3" disabled={!message && !attachment}>
                    <BsSend size={20} />
                </Button>
            </Form>
            <AttachmentModal
                show={showAttachmentModal}
                attachment={attachment}
                onCancel={() => {
                    setShowAttachmentModal(false)
                    setAttachment(null)
                }}
                onAttachmentChange={handleAttachmentChange}
                onConfirm={handleUploadFile}
            />
        </>
    );
};

export default MessageInput;
