// Imports
import React, {useContext} from 'react';
import {Accordion, Button} from 'react-bootstrap';
import {useNavigate} from "react-router-dom";

// Components
import {TicketActions} from '@components'

// Styles
import {BsInfoCircleFill} from 'react-icons/bs'

// Utils
import {Role, TicketStatus} from '@utils';

// Contexts
import {SessionContext} from "@contexts";

const TicketAccordion = ({ticket, handleUpdate}) => {
    const navigate = useNavigate();
    const {session} = useContext(SessionContext)

    const surveyActionMessage = session.details.authorities[0] === Role.CUSTOMER ? "SENT" : "RECEIVED"
    const surveyMessage = ticket.survey ?
        <span>{" - "}<span className="text-success">SURVEY {surveyActionMessage} YET</span></span> :
        <span>{" - "}<span className="text-danger">SURVEY NOT {surveyActionMessage} YET</span></span>

    return (
        <Accordion className="mb-4 shadow-sm border-start border-3 rounded-3 border-primary">
            <Accordion.Item eventKey={ticket.id}>
                <Accordion.Header>
                    <div className="d-flex justify-content-between align-items-center w-100 my-3">
                        <div>
                            <small>Ticket #{ticket.id}</small>
                            <h5><strong>{ticket.title}</strong></h5>
                        </div>
                        <h6 className='me-4'>
                            {ticket.status.replaceAll("_", " ")}
                            {ticket.status === TicketStatus.IN_PROGRESS && ` - ${ticket.priorityLevel}`}
                            {ticket.status === TicketStatus.RESOLVED && surveyMessage}
                        </h6>
                    </div>
                </Accordion.Header>
                <Accordion.Body className={"my-3"}>
                    <div className={"d-flex justify-content-between"}>
                        <div className={"w-50"}>
                            <small>Description</small>
                            <p><strong>{ticket.description}</strong></p>
                        </div>

                        <div className={"w-50 text-end"}>
                            <small>Created at</small>
                            <p><strong>{new Date(ticket.createdDate).toLocaleString()}</strong></p>
                            {ticket.closedDate && <>
                                <small>Closed at</small>
                                <p>
                                    <strong>{new Date(ticket.closedDate).toLocaleString()}</strong>
                                </p>
                            </>}
                        </div>
                    </div>

                    <div className={"w-100 border rounded-3 opacity-25 my-4"}/>

                    <div className={"d-flex justify-content-between"}>
                        <div className={"w-50"}>
                            <small>Product</small>
                            <p className={"m-0"}><small><strong>{ticket.product.brand}</strong></small></p>
                            <p className={"m-0"}><small><strong>{ticket.product.name}</strong></small></p>
                            <p><small><strong>EAN #{ticket.product.ean}</strong></small></p>
                        </div>
                        <div className={"w-50 text-end"}>
                            <small>Customer</small>
                            <p className={"m-0"}><strong>{ticket.customer.name} {ticket.customer.surname}</strong></p>
                            <p><small><strong>{ticket.customer.email}</strong></small></p>
                        </div>
                    </div>

                    <div className={"w-100 border rounded-3 opacity-25 my-4"}/>

                    {(ticket.status === TicketStatus.RESOLVED || ticket.status === TicketStatus.CLOSED) &&
                        <div className={"d-flex justify-content-between"}>
                            {ticket.resolvedDescription &&
                                <div className='w-50'>
                                    <small>Description of ticket resolution</small>
                                    <p className={"m-0"}><small><strong>{ticket.resolvedDescription}</strong></small>
                                    </p>
                                </div>
                            }
                            {ticket.survey &&
                                <div className='w-50 text-end'>
                                    <small>Survey Response</small>
                                    <p className={"m-0 mt-2"}><small>Service
                                        Valuation:</small>{" "}<strong>{ticket.survey.serviceValuation}</strong></p>
                                    <p>
                                        <small>Professionality:</small><strong>{" "}{ticket.survey.professionality}</strong>
                                    </p>

                                    <small>Comments from customer</small>
                                    <p className={"m-0"}><small><strong>{ticket.survey.comment}</strong></small></p>
                                </div>
                            }
                        </div>
                    }


                    <div className='d-flex align-items-center mt-4'>
                        <Button variant='primary' className='d-flex align-items-center py-2 px-3 rounded-3 fw-semibold'
                                onClick={() => navigate(`/tickets/${ticket.id}`)}>
                            <BsInfoCircleFill size={15} className="me-2"/>
                            Go to Ticket Details
                        </Button>

                        <TicketActions ticket={ticket} handleUpdate={handleUpdate}/>
                    </div>
                </Accordion.Body>
            </Accordion.Item>
        </Accordion>
    );
};

export default TicketAccordion;
