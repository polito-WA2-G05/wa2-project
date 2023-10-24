// Imports
import {useContext, useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {Button, Card, Col, Row} from "react-bootstrap";

// Components
import {Loader} from "@components/layout";
import {InfoCard, TicketActions} from "@components";

// Services
import api from "@services";

import {TicketStatus} from "@utils";
import {SessionContext} from "@contexts";

const TicketGeneralInfo = ({ticket}) => (
    <div>
        {[
            {label: "Description", value: ticket.description},
            {label: "Specialization", value: ticket.specialization.name},
            {
                label: "Created At",
                value: new Date(ticket.createdDate).toLocaleString(),
            },
            {
                label: "Closed At",
                value: ticket.closedDate ? new Date(ticket.closedDate).toLocaleString() : "Not closed yet",
            },
        ].map((info) => (
            <Card.Text key={`ticket-${ticket.id}-${info.label}`}>
                <strong>{info.label}</strong>
                <p>{info.value}</p>
            </Card.Text>
        ))}
    </div>
);

const TicketStatusInfo = ({ticket}) => (
    <div className="w-50">
        <span>Status</span>
        <h5 className="fw-bold mb-3">{ticket.status}</h5>
        {ticket.status === TicketStatus.RESOLVED && ticket.resolvedDescription &&
            <>
                <span>Description of ticket resolution</span>
                <h5>{ticket.resolvedDescription}</h5>
            </>
        }

        {[TicketStatus.OPEN, TicketStatus.IN_PROGRESS, TicketStatus.REOPENED]
                .includes(ticket.status) &&
            <>
                <span>Expert</span>
                <h5 className="fw-bold mb-3">
                    {ticket.expert ? "Assigned" : "Not assigned"}
                </h5>
            </>
        }

        {ticket.status === TicketStatus.IN_PROGRESS &&
            <>
                <span>Priority</span>
                <h5 className="fw-bold">{ticket.priorityLevel ?? "None"}</h5>
            </>
        }
    </div>
);

const TicketCustomerInfo = ({customer, ticketId}) => (
    <div className="my-5">
        <h4 className="fw-bold mb-3">Customer Info</h4>
        {[
            {label: "Full name", value: `${customer.name} ${customer.surname}`},
            {label: "Email", value: customer.email},
        ].map((info) => (
            <Card.Text key={`ticket-${ticketId}-${info.label}`}>
                <strong>{info.label}</strong>
                <p>{info.value}</p>
            </Card.Text>
        ))}
    </div>
);

const TicketProductInfo = ({product, ticketId}) => (
    <div className="my-5">
        <h4 className="fw-bold mb-3">Product Info</h4>
        {[
            {label: "EAN", value: product.ean},
            {label: "Name", value: product.name},
            {label: "Brand", value: product.brand},
        ].map((info) => (
            <Card.Text key={`ticket-${ticketId}-${info.label}`}>
                <strong>{info.label}</strong>
                <p>{info.value}</p>
            </Card.Text>
        ))}
    </div>
);

const Ticket = () => {
    const {session, onError} = useContext(SessionContext)

    const [ticket, setTicket] = useState(null);
    const [loading, setLoading] = useState(true);

    const {ticketId} = useParams();
    const navigate = useNavigate();

    const handleUpdate = (ticket) => {
        setTicket(ticket)
    }

    useEffect(() => {
        if (loading) {
            api.ticket.getTicket(session.details.authorities[0].toLowerCase(), ticketId)
                .then(ticket => setTicket(ticket))
                .catch((err) => err?.status !== 404 && onError(err))
                .finally(() => setLoading(false))
        }
    }, []) // eslint-disable-line

    const onGoBack = () => navigate(-1, {replace: true})

    if (!loading)
        return (
            <Row className="my-5">
                {ticket ? (
                    <>
                        <Col xs={2} className={"d-flex justify-content-center align-items-start"}>
                            <Button variant={"outline-primary"} onClick={onGoBack}
                                    className="py-2 px-5 rounded-3 fw-semibold mx-auto">
                                Go Back
                            </Button>
                        </Col>
                        <Col xs={12} lg={8}>
                            <InfoCard headerTitle={`Ticket ${ticket.id} Info`} contentTitle={`${ticket.title}`}>
                                <div className="d-flex justify-content-between align-items-start">
                                    <div className="w-50">
                                        <TicketGeneralInfo ticket={ticket}/>
                                        <TicketCustomerInfo customer={ticket.customer} ticketId={ticketId}/>
                                        <TicketProductInfo product={ticket.product} ticketId={ticketId}/>
                                    </div>
                                    <TicketStatusInfo ticket={ticket}/>
                                </div>
                                <TicketActions ticket={ticket} handleUpdate={handleUpdate}/>
                            </InfoCard>
                        </Col>
                    </>
                ) : (
                    <div className="d-flex flex-column align-items-center">
                        <h4 className={"fw-bold"}>Ticket #{ticketId} not found</h4>
                        <Button onClick={onGoBack}
                                className="py-2 px-5 rounded-3 my-5 fw-semibold">
                            Go Back
                        </Button>
                    </div>
                )}
            </Row>
        );

    return <Loader/>
};

export default Ticket;