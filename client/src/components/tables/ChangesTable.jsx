// Imports
import React from 'react'
import { Table } from "react-bootstrap";
import { useNavigate } from 'react-router-dom';

const ChangesTable = ({ changes }) => {
    const navigate = useNavigate()

    const changeInfo = ["Ticket", "From Status", "To Status", "Expert", "Updated at"]

    const navigateToTicket = (id) => {
        navigate(`/tickets/${id}`)
    }

    return (
        <Table responsive striped bordered hover>
            <thead>
                <tr>
                    {changeInfo.map((label, idx) =>
                        <th key={`change-label-${idx}`}>{label}</th>)}
                </tr>
            </thead>
            <tbody>
                {changes.map(change => {
                    const onTicketClick = () => navigateToTicket(change.ticket.id)

                    return <tr key={`change-${change.id}`}>
                        <td className='link-primary' onClick={onTicketClick} style={{ cursor: "pointer" }}>
                            {change.ticket.id}
                        </td>
                        <td>{change.fromStatus ?? "-"}</td>
                        <td>{change.toStatus}</td>
                        <td>{change.expert?.username ?? "Not assigned"}</td>
                        <td>{new Date(change.timestamp).toLocaleString()}</td>
                    </tr>
                })}
            </tbody>
        </Table>
    )
}

export default ChangesTable