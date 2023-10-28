// Imports
import React, { useState, useEffect, useContext } from 'react';
import { Button, Modal } from 'react-bootstrap';

//Components
import { Loader } from "@components/layout"

// Services
import api from '@services'

// Hooks
import { useNotification } from '@hooks'

import {SessionContext} from "@contexts"

const UploadedAttachmentsModal = ({ show, onCancel, ticket }) => {
    const [loading, setLoading] = useState(true);
    const [attachments, setAttachments] = useState(null);

    const {role} = useContext(SessionContext)

    const notify = useNotification();

    useEffect(() => {
        if (loading)
            api.utils.getAttachments(role, ticket.id)
                .then(attachments => setAttachments(attachments))
                .catch(err => notify.error(err.detail ?? err))
                .finally(() => setLoading(false))

    }, [])

    const handleDownload = (attachment) => {
        api.utils.getAttachment(role, ticket.id, attachment.name)
            .then((res) => {
                const blob = new Blob([res], { type: 'application/pdf' }); // Specify the correct content type
                const url = window.URL.createObjectURL(blob)
                const link = document.createElement('a')
                link.href = url
                link.setAttribute('download', attachment.name)
                document.body.appendChild(link)
                link.click()
                link.parentNode.removeChild(link)
            })
    }


    
    if (loading) return <Loader />

    return (
        <Modal show={show} onHide={onCancel} centered>
            <Modal.Header>
                <Modal.Title>Attachments</Modal.Title>
            </Modal.Header>
            <Modal.Body className={"my-5"}>

                {attachments.length === 0 ? <p>No uploaded attachments</p> :
                    attachments?.map((attachment) =>
                    <p key={attachment.name} className="link-primary" style={{cursor: 'pointer'}} onClick={() => handleDownload(attachment)}>{attachment.name}</p>
                )}

            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={onCancel}>
                    Close
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default UploadedAttachmentsModal;
