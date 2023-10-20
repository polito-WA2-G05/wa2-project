// Imports
import React, { useState } from 'react';
import { Form, Button } from 'react-bootstrap';

// Styles
import { BsSend } from "react-icons/bs";

const MessageInput = ({setTs}) => {
  const [message, setMessage] = useState('');
  
  const handleInsert = (e) => {
    setMessage(e.target.value); 
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setTs(new Date())
    // Esegui la tua chiamata API per inviare il messaggio qui
  };

  return (
      <Form onSubmit={handleSubmit} className="mt-4 d-flex flex-row justify-content-between">
              <Form.Control
              as="textarea"
              placeholder="Type something..."
              value={message} 
              onChange={handleInsert}
              />
              <Button type="submit" className="ms-3 px-3">
                  <BsSend size={20} />
              </Button>
      </Form>
  );
};

export default MessageInput;
