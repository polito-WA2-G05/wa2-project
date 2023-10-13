// Imports
import { useEffect, useState } from "react";
import { useLocation, useParams, useNavigate } from "react-router-dom";
import { Spinner, Card, Button } from "react-bootstrap";

// Components
import { InfoCard } from "@components";

import {useSessionStorage} from "@hooks"

// Services
import api from "@services";

// Hooks
import { useNotification } from "@hooks";

const TicketGeneralInfo = ({ ticket }) => (
	<div>
		{[
			{ label: "Description", value: ticket.description },
			{ label: "Specialization", value: ticket.specialization.name },
			{
				label: "Created At",
				value: new Date(ticket.createdDate).toLocaleString(),
			},
			{
				label: "Closed At",
				value: new Date(ticket.closedDate).toLocaleString(),
			},
		].map((info) => (
			<Card.Text key={`ticket-${ticket.id}-${info.label}`}>
				<strong>{info.label}</strong>
				<p>{info.value}</p>
			</Card.Text>
		))}
	</div>
);

const TicketStatusInfo = ({ ticket }) => (
	<div className="w-50">
		<span>Status</span>
		<h5 className="fw-bold mb-3">{ticket.status}</h5>

		<span>Expert</span>
		<h5 className="fw-bold mb-3">
			{ticket.expert ? "Assigned" : "Not assigned"}
		</h5>

		<span>Priority</span>
		<h5 className="fw-bold">{ticket.priorityLevel ?? "None"}</h5>
	</div>
);

const TicketCustomerInfo = ({ customer, ticketId }) => (
	<div className="my-5">
		<h4 className="fw-bold mb-3">Customer Info</h4>
		{[
			{ label: "Full name", value: `${customer.name} ${customer.surname}` },
			{ label: "Email", value: customer.email },
		].map((info) => (
			<Card.Text key={`ticket-${ticketId}-${info.label}`}>
				<strong>{info.label}</strong>
				<p>{info.value}</p>
			</Card.Text>
		))}
	</div>
);

const TicketProductInfo = ({ product, ticketId }) => (
	<div className="my-5">
		<h4 className="fw-bold mb-3">Product Info</h4>
		{[
			{ label: "EAN", value: product.ean },
			{ label: "Name", value: product.name },
			{ label: "Brand", value: product.brand },
		].map((info) => (
			<Card.Text key={`ticket-${ticketId}-${info.label}`}>
				<strong>{info.label}</strong>
				<p>{info.value}</p>
			</Card.Text>
		))}
	</div>
);

const Ticket = () => {
	const {session} = useSessionStorage()
	const { ticketId } = useParams();
	const location = useLocation();
	const { state } = location;

	// Enable this comment
	const [ticket, setTicket] = useState(null);
	const [loading, setLoading] = useState(true);

	const notify = useNotification();

	useEffect(() => {
		if (loading) {
			if (!state?.ticket) {
				api.ticket
					.getTicket(session.details.authorities[0].toLowerCase(), ticketId)
					.then((ticket) => setTicket(ticket))
					.catch((err) => {
						setTicket(null);
						if (err.status !== 404) notify.error(err.detail ?? err);
					})
					.finally(() => setLoading(false));
			} else {
				setTicket(state.ticket);
				setLoading(false);
			}
		}
	}, []);

	if (!loading)
		return (
			<>
				{ticket ? (
					<InfoCard
						headerTitle={`Ticket ${ticket.id} Info`}
						contentTitle={`${ticket.title}`}
					>
						{/* <InfoCard > */}
						<div className="d-flex justify-content-between align-items-start">
							<div className="w-50">
								<TicketGeneralInfo ticket={ticket} />
								<TicketCustomerInfo customer={ticket.customer} ticketId={ticketId} />
								<TicketProductInfo product={ticket.product} ticketId={ticketId}/>
							</div>
							<TicketStatusInfo ticket={ticket} />
						</div>
						<TicketActions ticket={ticket} />
					</InfoCard>
				) : (
					<h3 className={"fw-bold fs-2 text-center"}>
						Ticket #{ticketId} not found
					</h3>
				)}
			</>
		);

	return (
		<div className="d-flex justify-content-center align-items-center w-100">
			<Spinner
				animation="border"
				size="xl"
				as="span"
				role="status"
				aria-hidden="true"
				className="me-2"
			/>
			<h2>Loading...</h2>
		</div>
	);
};

export default Ticket;

const TicketActions = ({ ticket }) => {
	const navigate = useNavigate();

	return (
		<>
			<Button
				className="me-3"
				onClick={() => navigate("/me/edit", { replace: true })}
				variant="danger"
			>
				Cancel ticket
			</Button>
			<Button
				onClick={() => navigate("/me/edit", { replace: true })}
				variant="warning"
			>
				Reopen ticket
			</Button>
		</>
	);
};
