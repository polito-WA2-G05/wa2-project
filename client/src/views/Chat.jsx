import React, {useState} from 'react';
import { Col, Row } from 'react-bootstrap';
import { ChatMessage, MessageInput } from '@components';

const Chat = () => {
    const [ts, setTs] = useState(null)
    return (
        <Row className="justify-content-center my-5">
            <Col xs={12} lg={8} className="d-flex flex-column p-3">
        
                    <ChatMessage received={true} message="Scemo chi legge" ts={ts}/>
                    <ChatMessage received={true} message="Scemo chi legge" ts={ts}/>
                    <ChatMessage received={true} message="Scemo chi legge" ts={ts}/>
                    <ChatMessage received={true} message="Scemo chi legge" ts={ts}/>
                    <ChatMessage received={false} message="Scemo chi legge" ts={ts}/>
                    <ChatMessage received={false} message="Scemo chi legge" ts={ts}/>
    
                    <MessageInput setTs={setTs}/>
            </Col>
        </Row>
    );
};

export default Chat;
