// Imports
import React from 'react'
import { useNavigate } from 'react-router-dom';
import { Col, Table } from "react-bootstrap";

const ExpertsTable = ({ experts }) => {

    const expertsInfo = ["Expert username", "Currently working on", "Specialization"]
    return (
        <Col xs={12}>
            <Table responsive striped bordered hover>
                <thead>
                    <tr>{expertsInfo.map((label, idx) => <th key={`change-label-${idx}`}>{label}</th>)}</tr>
                </thead>
                <tbody>
                    {experts.map((expert, idx) => (
                        <tr key={`expert-${idx}`}>
                            <td>{expert.username}</td>
                            <td>{expert.workingOn}</td>
                            <td>{expert.specializations.map((spec) => spec.name).join(", ")}</td>
                        </tr>
                    ))}
                </tbody>
            </Table>
        </Col>
    )
}

export default ExpertsTable