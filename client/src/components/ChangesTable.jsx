// Imports
import React from 'react'
import { useNavigate } from 'react-router-dom';
import { Col, Table } from "react-bootstrap";

const ChangesTable = ({ changes }) => {
    const navigate = useNavigate()

    const changeInfo = ["Ticket", "From Status", "To Status", "Expert", "Updated at"]

    return (
        <Col xs={12}>
            <Table responsive striped bordered hover>
                <thead>
                    <tr>{changeInfo.map((label, idx) => <th key={`change-label-${idx}`}>{label}</th>)}</tr>
                </thead>
                <tbody>
                    {changes.map((change, idx) => (
                        <tr key={`change-${idx}`} onClick={() => navigate(`/tickets/${change.ticket.id}`)}>
                            <td>{change.ticket.id}</td>
                            <td>{change.fromStatus ?? "-"}</td>
                            <td>{change.toStatus}</td>
                            <td>{change.expert?.username ?? "Not assigned"}</td>
                            <td>{new Date(change.timestamp).toLocaleString()}</td>
                        </tr>
                    ))}
                </tbody>
            </Table>
        </Col>
    )
}

export default ChangesTable