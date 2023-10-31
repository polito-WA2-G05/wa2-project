// Imports
import React, {useContext, useEffect, useState} from "react";
import Select from "react-select"
import {useNavigate, useSearchParams} from "react-router-dom";
import {Button, Col} from "react-bootstrap";

// Components
import {Loader} from "@components/layout";
import {TicketAccordion} from "@components";

// Services
import api from '@services'

//Utils
import {Role, TicketStatus} from "@utils";

// Contexts
import {SessionContext} from "@contexts";

const Tickets = () => {
    const {role, onError} = useContext(SessionContext)

    const [tickets, setTickets] = useState([])
    const [loading, setLoading] = useState(true)
    const [statusFilter, setStatusFilter] = useState([])

    const [searchParams] = useSearchParams()

    const filteredTickets = tickets.filter(ticket =>
        statusFilter.length === 0 ? true :
            statusFilter.includes(ticket.status)
    )

    const handleUpdate = (updatedTicket) => {
        setTickets((oldTickets) => {
            const index = oldTickets.findIndex(ticket =>
                ticket.id === updatedTicket.id
            )

            const newTickets = [...oldTickets];
            newTickets[index] = updatedTicket;

            return newTickets;
        });
    };

    const handleFilterChange = (selectedStates) => {
        setStatusFilter(selectedStates.map(status => status.label))
    }

    useEffect(() => {
        if (loading)
            api.ticket.getTickets(role, searchParams.get("product"))
                .then(tickets => {
                    const sortedTickets = tickets.sort((el1, el2) => {
                        const el1CreatedAt = new Date(el1.createdDate)
                        const el2CreatedAt = new Date(el2.createdDate)
                        return el2CreatedAt - el1CreatedAt
                    })
                    setTickets(sortedTickets)
                })
                .catch((err) => err?.status !== 404 && onError(err))
                .finally(() => setLoading(false))
    }, []) // eslint-disable-line

    if (!loading)
        return (
            <>
                {tickets.length === 0 ? <NotFoundView role={role}/>
                    : <Col xs={12} lg={8} className="my-5">
                        <div className={"my-5 text-center"}>
                            <h1 className={"fw-bold"}>
                                {role === Role.MANAGER ? "All Tickets" : "Your Tickets"}
                            </h1>
                            {searchParams.get("product") && <h5>For product #{searchParams.get("product")}</h5>}
                        </div>

                        {role !== Role.EXPERT &&
                            <StatusFilter onFilterChange={handleFilterChange}/>
                        }

                        {filteredTickets.length !== 0 ? filteredTickets.map(ticket =>
                                <TicketAccordion
                                    key={ticket.id}
                                    handleUpdate={handleUpdate}
                                    ticket={ticket}
                                    setTickets={setTickets}
                                />
                            ) :
                            <h4 className={"fw-bold text-center"}>
                                No tickets with selected filters have been found
                            </h4>
                        }
                    </Col>
                }
            </>
        );

    return <Loader/>
};

const StatusFilter = ({onFilterChange}) => {
    return <Select
        className="my-5 w-50 mx-auto"
        name={"filterStatus"}
        onChange={onFilterChange}
        placeholder={"Filter by status"}
        options={TicketStatus.allCases}
        isMulti={true}
        styles={{
            container: (baseStyles) => ({
                ...baseStyles,
                zIndex: 99
            }),
        }}
    />

}

const NotFoundView = (role) => {
    const navigate = useNavigate()

    const onGoBack = () => navigate(-1, {replace: true})

    return <div className="d-flex flex-column align-items-center text-center">
        <h4 className={"fw-bold"}>
            {role === Role.EXPERT ? "No tickets assigned to you"
                : "No tickets have been found"}
        </h4>
        <Button onClick={onGoBack} className="py-2 px-5 rounded-3 my-5 fw-semibold">
            Go Back
        </Button>
    </div>
}

export default Tickets;
