// Imports
import {useContext, useEffect, useState} from 'react'
import {Col, Form, Row} from 'react-bootstrap';

// Components
import {Loader} from "@components/layout"
import {ChangesTable} from '@components'

// Services
import api from '@services'

import {SessionContext} from '@contexts';

const Changes = () => {
    const [changes, setChanges] = useState([]);
    const [loading, setLoading] = useState(true);

    const [ticket, setTicket] = useState(null);
    const [expert, setExpert] = useState(null)

    const {onError} = useContext(SessionContext)

    const filteredChanges = changes.filter(change => {
        if (ticket == null && expert == null)
            return true;
        else if (ticket && expert == null)
            return String(change.ticket.id).startsWith(ticket)
        else if (ticket === null && expert)
            return change.expert?.username.startsWith(expert)

        return String(change.ticket.id).startsWith(ticket) && change.expert?.username.startsWith(expert);
    })

    useEffect(() => {
        if (loading)
            api.ticket.getChanges()
                .then(changes => setChanges(changes))
                .catch(onError)
                .finally(() => setLoading(false))
    }, []) // eslint-disable-line

    const handleExpertChange = (expert) => {
        setExpert(expert || null)
    }

    const handleTicketChange = (ticket) => {
        setTicket(ticket || null)
    }

    if (!loading)
        return (
            <>
                {changes.length === 0 ? <h4 className={"text-center fw-bold"}>No changes have been found</h4> :
                    <Col xs={12} lg={11} className='text-center align-self-start'>
                        <h1 className={"fw-bold my-5"}>Changes Log</h1>
                        <Filters onTicketChange={handleTicketChange} onExpertChange={handleExpertChange}/>
                        {filteredChanges.length ?
                            <ChangesTable changes={filteredChanges}/>
                            : <h4 className={"my-5"}>No changes found for active filters</h4>
                        }
                    </Col>
                }
            </>
        )

    return <Loader/>
}

const Filters = ({onTicketChange, onExpertChange}) => (
    <Row className='my-5'>
        <Col xs={12} lg={6}>
            <Form.Control
                id="ticket-filter"
                type="text"
                placeholder="Filter by Ticket Id"
                onChange={(e) => onTicketChange(e.target.value)}
            />
        </Col>
        <Col xs={12} lg={6}>
            <Form.Control
                id="expert-filter"
                type="text"
                placeholder="Filter by Expert Username"
                onChange={(e) => onExpertChange(e.target.value)}
            />
        </Col>
    </Row>
)

export default Changes