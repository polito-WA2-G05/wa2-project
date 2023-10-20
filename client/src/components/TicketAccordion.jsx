// Imports
import React from 'react';
import { Accordion, Button, Row, Col } from 'react-bootstrap';
import { useNavigate } from "react-router-dom";

// Components
import { TicketActions } from '@components'

// Styles
import { BsInfoCircleFill } from 'react-icons/bs'

// Utils
import { TicketStatus, Role } from '@utils';

import { useSessionStorage } from "@hooks"

const TicketAccordion = ({ ticket, handleUpdate }) => {
  const navigate = useNavigate();
  const { session } = useSessionStorage()

  const surveyActionMessage = session.details.authorities[0] === Role.CUSTOMER ? "SENT" : "RECEIVED"
  const surveyMessage = ticket.survey ? ` - SURVEY ${surveyActionMessage}` : ` - SURVEY NOT ${surveyActionMessage} YET`

  return (
    <Accordion className="mb-4 shadow">
      <Accordion.Item eventKey={ticket.id}>
        <Accordion.Header>
          <div className="d-flex justify-content-between align-items-center w-100">
            <h6>Ticket {ticket.id}: {ticket.title}</h6>
            <h6 className='me-4'>
              {ticket.status}
              {ticket.status === TicketStatus.IN_PROGRESS && ticket.priorityLevel && ` - ${ticket.priorityLevel}`}
              {ticket.status === TicketStatus.RESOLVED && surveyMessage}
            </h6>
          </div>
        </Accordion.Header>
        <Accordion.Body>
          <div className='mt-3'>
            <p><strong>Description:</strong></p>
            <p>{ticket.description}</p>
          </div>
          <Row className="mt-5">
            <Col xs={12} lg={8} >
              <p><strong>Customer:</strong> {ticket.customer.name} {ticket.customer.surname} ({ticket.customer.email})</p>
              <p><strong>Product:</strong> {ticket.product.name} by {ticket.product.brand} (EAN: {ticket.product.ean})</p>
              {(ticket.status === TicketStatus.RESOLVED || ticket.status === TicketStatus.CLOSED) &&
                <div>
                  {
                    ticket.resolvedDescription &&
                    <div className='my-5'>
                      <span className='fw-bold'>Description of ticket resolution</span>
                      <p>{ticket.resolvedDescription}</p>
                    </div>
                  }
                  {
                    ticket.survey &&
                    <div className='my-5 d-flex flex-column'>
                      <span className='fw-bold'>Survey Response</span>
                      <span>Service Valuation: {ticket.survey.serviceValuation}</span>
                      <span>Professionality: {ticket.survey.professionality}</span>
                      <div className='mt-3'>
                        <p><strong>Comments from customer </strong></p>
                        <p>{ticket.survey.comment}</p>
                      </div>
                    </div>
                  }
                </div>
              }
            </Col>
            <Col xs={12} lg={4}>
              <p><strong>Created Date:</strong> {new Date(ticket.createdDate).toLocaleString()}</p>
              <p><strong>Closed Date:</strong> {ticket.closedDate ? new Date(ticket.closedDate).toLocaleString() : 'Not Closed Yet'}</p>
            </Col>
          </Row>
          <div className='d-flex align-items-center'>
            <Button className='me-3' onClick={() => navigate(`/tickets/${ticket.id}`)} variant='primary'>
              <BsInfoCircleFill size={15} className="me-2" />
              Go to Ticket Details
            </Button>
            <TicketActions ticket={ticket} handleUpdate={handleUpdate} />
          </div>
        </Accordion.Body>
      </Accordion.Item>
    </Accordion>
  );
};

export default TicketAccordion;
