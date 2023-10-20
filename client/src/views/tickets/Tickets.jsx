// Imports
import React, { useState, useEffect } from "react";
import Select from "react-select"
import { useSearchParams } from "react-router-dom";
import { Row, Col, Spinner } from "react-bootstrap";

// Components
import { TicketAccordion } from "@components";

// Services
import api from '@services'

// Hooks
import { useSessionStorage, useNotification } from "@hooks"

//Utils
import { TicketStatus, Role } from "@utils";

const Tickets = () => {
	const [loading, setLoading] = useState(true)
	const [tickets, setTickets] = useState([])
	const [statusFilter, setStatusFilter] = useState([])

	const [searchParams] = useSearchParams()

	const { session } = useSessionStorage()
	const notify = useNotification();

	const states = [
		{ label: TicketStatus.OPEN, value: 0 },
		{ label: TicketStatus.REOPENED, value: 1 },
		{ label: TicketStatus.IN_PROGRESS, value: 2 },
		{ label: TicketStatus.CANCELLED, value: 3 },
		{ label: TicketStatus.RESOLVED, value: 4 },
		{ label: TicketStatus.CLOSED, value: 5 }
	]

	const handleUpdate = (updatedTicket) => {
		setTickets((oldTickets) => {
			const index = oldTickets.findIndex((ticket) => ticket.id === updatedTicket.id);

			const newTickets = [...oldTickets];
			newTickets[index] = updatedTicket;

			console.log("index: " + index)
			console.log("updatedTicket: " + updatedTicket)
			console.log("newTickets: " + newTickets)

			return newTickets;
		});
	};

	const handleFilterStatus = (selectedStates) => {
		setStatusFilter(selectedStates.map(status => status.label))
	}

	useEffect(() => {
		if (loading)
			api.ticket.getTickets(session.details.authorities[0].toLowerCase(), searchParams.get("product"))
				.then((tickets) => {
					setTickets(tickets)
				})
				.catch((err) => {
					if (err.status === 404) {
						setTickets([])
					} else { notify.error(err.detail ?? err) }
				})
				.finally(() => setLoading(false))
	}, [])

	if (loading)
		return <div className="d-flex justify-content-center align-items-center w-100">
			<Spinner animation='border' size='xl' as='span' role='status' aria-hidden='true' className='me-2' />
			<h2>Loading...</h2>
		</div>

	return (
		<Row>
			<Col xs={12} lg={8} className="mx-auto">
				<div className="d-flex flex-column justify-content-center">
					{tickets.length === 0 ? (
						<h1 className="text-center mb-4">
							{session.details.authorities[0] === Role.EXPERT ? " No tickets assigned to you" : "No tickets have been found"}
						</h1>
					) : (
						<>
							<h1 className="text-center mb-4">{session.details.authorities[0] === Role.MANAGER ? "All Tickets" : "Your Tickets"}</h1>

							{session.details.authorities[0] !== Role.EXPERT &&
								<Select
									className="my-5 w-50 mx-auto"
									name={"filterStatus"}
									onChange={handleFilterStatus}
									placeholder={"Filter by status"}
									options={states}
									isMulti={true}
									styles={{
										container: (baseStyles, state) => ({
										  ...baseStyles,
										  zIndex: 99
										}),
									  }}
								/>
							}

							{tickets.filter(ticket =>
								statusFilter.length === 0 ? true :
									statusFilter.includes(ticket.status))
								.map((ticket) => (
									<TicketAccordion handleUpdate={handleUpdate} key={ticket.id} ticket={ticket} setTickets={setTickets} />
								))}
						</>
					)}
				</div>
			</Col>
		</Row>
	);
};

export default Tickets;
