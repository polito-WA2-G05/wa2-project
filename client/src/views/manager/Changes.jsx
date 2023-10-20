// Imports
import { useState, useEffect } from 'react'
import { Spinner, Form, Row, Col } from 'react-bootstrap';
import Select from "react-select"

// Components
import { ChangesTable } from '@components'

// Services
import api from '@services'

// Hooks
import { useNotification } from '@hooks';

const Changes = () => {
    const [changes, setChanges] = useState([]);
    const [loading, setLoading] = useState(true);

    const [ticketIdFilter, setIdFilter] = useState(null);
    const [expertFilter, setUsername] = useState(null)

    const notify = useNotification();

    useEffect(() => {
        if (loading) {
            api.ticket.getChanges()
                .then(changes => {
                    setChanges(changes)
                })
                .catch(err => {
                    if (err.status === 404) {
                        setChanges([])
                    } else { notify.error(err.detail ?? err) }
                })
                .finally(() => setLoading(false))
        }
    }, [])

    const handleUsernameChanged = (e) => {
        const username = e.target.value
        if (username) setUsername(username)
        else setUsername(null)
    }

    const handleTicketIdChanged = (e) => {
        const idString = e.target.value
        if (idString) setIdFilter(parseInt(idString))
        else setIdFilter(null)
    }


    if (!loading)
        return (
            <div className='text-center'>
                {changes.length === 0 ? <h3 className={"fw-bold fs-2 mb-4"}>No changes found</h3> :
                    <>
                        <h3 className={"fw-bold mb-4 fs-2 text-center"}>All changes</h3>
                        <Row className='my-4'>
                            <Col xs= {12} lg={6}>
                                <Form.Control
                                    className='d-flex flex-column my-4 mx-auto'
                                    type="text"
                                    placeholder="Filter by ticket ID"
                                    onChange={handleTicketIdChanged}
                                />
                            </Col>
                            <Col xs= {12} lg={6}>
                                <Form.Control
                                    className='d-flex flex-column my-4 mx-auto'
                                    type="text"
                                    placeholder="Filter by expert username"
                                    onChange={handleUsernameChanged}
                                />
                            </Col>
                        </Row>
                        <ChangesTable changes={
                            changes.filter((change) => {
                                if (ticketIdFilter == null && expertFilter == null) {
                                    return true;
                                } else if (ticketIdFilter && expertFilter == null) {
                                    return change.ticket.id === ticketIdFilter;
                                } else if (ticketIdFilter === null && expertFilter) {
                                    return change.expert?.username.startsWith(expertFilter);
                                }
                                return change.ticket.id === ticketIdFilter && change.expert?.username.startsWith(expertFilter);
                            })} />
                    </>
                }
            </div>
        )

    return <div className="d-flex justify-content-center align-items-center w-100">
        <Spinner animation='border' size='xl' as='span' role='status' aria-hidden='true' className='me-2' />
        <h2>Loading...</h2>
    </div>


}

export default Changes