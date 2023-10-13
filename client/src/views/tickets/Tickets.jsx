// Imports
import React, { useState, useEffect } from "react";
import { Row, Col, Spinner } from "react-bootstrap";

// Components
import {TicketAccordion} from "@components";

// Services
import api from '@services'

// Hooks
import {useSessionStorage, useNotification} from "@hooks"

const Tickets = () => {
	const {session} = useSessionStorage()
	const notify = useNotification();
	const [loading, setLoading] = useState(true);
	const [tickets, setTickets] = useState([]);

	useEffect(() => {
		if (loading)
			api.ticket.getTickets(session.details.authorities[0].toLowerCase(), null)
			.then((tickets) => {
				setTickets(tickets)
			})
			.catch((err) => {
				if(err.status === 404){
					setTickets([])
				} else { notify.error(err.details ?? err)} 	
			})
			.finally(() => setLoading(false))
	}, [])
    
	if(loading)
	return <div className="d-flex justify-content-center align-items-center w-100">
        <Spinner animation='border' size='xl' as='span' role='status' aria-hidden='true' className='me-2' />
        <h2>Loading...</h2>
    </div>
	
	return (
		<Row>
			<Col xs={12} lg={8} className="mx-auto">
				<div className="d-flex flex-column justify-content-center">
					{tickets.length === 0 ? (
						<h1 className="text-center mb-4">No tickets have been found</h1>
					) : (
						<>
							<h1 className="text-center mb-4">Tickets</h1>
							{tickets.map((ticket) => (
								<TicketAccordion key={ticket.id} ticket={ticket} />
							))}
						</>
					)}
				</div>
			</Col>
		</Row>
	);
};

export default Tickets;
