import React from 'react';
import {Accordion, Button, Row, Col} from 'react-bootstrap';
import { useNavigate } from "react-router-dom";

// Hooks
import { useSessionStorage } from "@hooks";

const TicketAccordion = ({ ticket }) => {
  const navigate = useNavigate();
  const { session } = useSessionStorage();

  const userAuth = session.details.authorities[0];

  return (
    <Accordion className="mb-4 shadow">
      <Accordion.Item eventKey={ticket.id}>
      <Accordion.Header>
        <div className="d-flex justify-content-between align-items-center w-100">
            <h6>Ticket {ticket.id}: {ticket.title}</h6>
            <h6 className='me-4'>{ticket.status} {ticket.priorityLevel ? `- ${ticket.priorityLevel}` : ""}</h6>
        </div>
      </Accordion.Header>
      <Accordion.Body>
        <p><strong>Description:</strong> {ticket.description}</p>
    <Row className="mt-4">
        <Col xs={12} lg={8} >
        <p><strong>Customer:</strong> {ticket.customer.name} {ticket.customer.surname} ({ticket.customer.email})</p>
        <p><strong>Expert:</strong> {ticket.expert ? `${ticket.expert.name} (${ticket.expert.email})` : 'Not Assigned'}</p>
        <p><strong>Product:</strong> {ticket.product.name} by {ticket.product.brand} (EAN: {ticket.product.ean})</p>
        </Col>
        <Col xs={12} lg={4}>
        <p><strong>Created Date:</strong> {new Date(ticket.createdDate).toLocaleString()}</p>
        <p><strong>Closed Date:</strong> {ticket.closedDate ? new Date(ticket.closedDate).toLocaleString() : 'Not Closed Yet'}</p>
        </Col>
        </Row>
        {ticket.status === "OPEN" && userAuth === "Manager" ? (
          <>
            <Button onClick={()=> {}}>Resolve Ticket</Button>
            <Button className="mx-3" onClick={()=> {}}>Close Ticket</Button>
            <Button onClick={()=> {}}>Assign Expert</Button>
          </>
        ) : <></>}
        {ticket.status === "REOPENED" && userAuth === "Manager" ? (
        <>
          <Button onClick={()=> {}}>Close Ticket</Button>
          <Button className="mx-3" onClick={()=> {}}>Resolve Ticket</Button>
          <Button onClick={()=> {}}>Assign Different Expert</Button>
        </>
        ): <></>}
        {ticket.status === "RESOLVED" && userAuth === "Manager" ? <Button className="me-3" onClick={()=> {}}>Close Ticket</Button> : <></>}
        {ticket.status === "IN PROGRESS" && userAuth === "Expert" ? (
          <>
          <Button onClick={()=> {}}>Stop Ticket</Button>
          <Button className="mx-3" onClick={()=> {}}>Close Ticket</Button>
          <Button onClick={()=> {}}>Resolve Ticket</Button>
        </>
        ): <></>}
        {ticket.status === "CLOSED" && userAuth === "Customer" ? <Button className="ms-3" onClick={()=> {}}>Reopen Ticket</Button> : <></>}
        {ticket.status === "RESOLVED" && userAuth === "Customer" ? <Button className="ms-3" onClick={()=> {}}>Reopen Ticket</Button> : <></>}
        {userAuth === "Customer" ? <Button className="ms-3" onClick={()=> {}}>Cancel Ticket</Button> : <></>}
        <Button className="ms-3" onClick={() => navigate(`/tickets/${ticket.id}`)}>Go to Ticket Details</Button> 
      </Accordion.Body>
      </Accordion.Item>
    </Accordion>
  );
};

export default TicketAccordion;
