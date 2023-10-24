// Imports
import React, {useContext, useEffect, useRef, useState} from 'react';
import {Navigate, useNavigate, useParams} from "react-router-dom"
import {Button, Col, Row} from 'react-bootstrap';


// Components
import {Loader} from '@components/layout';
import {ChatMessage, MessageInput} from '@components';

// Hooks
// Utils
import {Role, TicketStatus} from "@utils"

// Services
import api from "@services"

import {NotificationContext, SessionContext} from '@contexts';
import {useSocket} from "@hooks";

const Chat = () => {
    const {session, role, onError} = useContext(SessionContext)
    const {sendNotification} = useContext(NotificationContext)

    const [loading, setLoading] = useState(true)
    const [ticket, setTicket] = useState(null)

    const {ticketId} = useParams()
    const navigate = useNavigate();

    const chatRef = useRef(null);


    const scrollToBottom = () => {
        if (chatRef.current) {
            chatRef.current.scrollTop = chatRef.current.scrollHeight;
        }
    };

    const {connected, messages, setInitialMessages, sendMessage} = useSocket({
        channel: "messages"
    })

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    useEffect(() => {
        if (loading)
            api.ticket.getTicket(role.toLowerCase(), ticketId)
                .then(ticket => setTicket(ticket))
                .catch((err) => {
                    onError(err)
                    setLoading(false)
                })
    }, []) // eslint-disable-line

    useEffect(() => {
        if (ticket)
            api.ticket.getMessagesHistory(role.toLowerCase(), ticket.id)
                .then(messages => setInitialMessages(messages))
                .catch(onError)
                .finally(() => setLoading(false))
    }, [ticket]) // eslint-disable-line

    const handleSendMessage = (text) => {
        const receiver = role === Role.CUSTOMER ?
            ticket.expert.id : ticket.customer.id

        const sender = role === Role.CUSTOMER ?
            ticket.customer.id : ticket.expert.id

        const msg = {
            text,
            timestamp: new Date(),
            isFromCustomer: role === Role.CUSTOMER,
            receiver,
            sender,
            ticket: ticket.id
        }

        sendMessage(msg, {includeLocally: true})
        sendNotification({receiver, text: `You have received a new message for the ticket ${ticket.id}`})
    }

    const received = (msg) => {
        if (msg.isFromCustomer && role === Role.CUSTOMER) {
            return false
        } else if (msg.isFromCustomer && role === Role.EXPERT) {
            return true
        } else if (!msg.isFromCustomer && role === Role.CUSTOMER) {
            return true
        } else if (!msg.isFromCustomer && role === Role.EXPERT) {
            return false
        }
    }

    if (!loading && connected)
        return <>
            {ticket ? (
                ticket.status !== TicketStatus.IN_PROGRESS ? <Navigate to={"/tickets"} replace/> : (
                    <Row className="my-5 align-self-start">
                        <Col xs={2} className={"d-flex justify-content-center align-items-start"}>
                            <Button variant={"outline-primary"} onClick={() => navigate(-1, {replace: true})}
                                    className="py-2 px-5 rounded-3 fw-semibold mx-auto">
                                Go Back
                            </Button>
                        </Col>
                        <Col xs={12} lg={8} className={"overflow-hidden py-2"}>
                            <h4 className="fw-bold text-center mb-5">Support chat for ticket #{ticketId}</h4>
                            <div ref={chatRef}
                                 className='d-flex flex-column align-items-end mb-3 overflow-scroll px-2'
                                 style={{height: "46vh", overflowX: "hidden"}}
                            >
                                {messages.map((message, index) => (
                                    <ChatMessage
                                        key={index}
                                        received={received(message)}
                                        message={message.text}
                                        timestamp={message.timestamp}
                                    />
                                ))}
                            </div>
                            <MessageInput sendMessage={handleSendMessage}/>
                        </Col>
                    </Row>
                )) : <div className="d-flex flex-column align-items-center">
                <h4 className={"fw-bold"}>Ticket #{ticketId} has not been found</h4>
                <Button onClick={() => navigate("/tickets", {replace: true})}
                        className="py-2 px-5 rounded-3 my-5 fw-semibold">
                    Go to Tickets
                </Button>
            </div>
            }
        </>

    return <Loader/>
}

export default Chat;
