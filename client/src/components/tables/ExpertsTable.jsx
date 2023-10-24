// Imports
import React from 'react'
import { Table } from "react-bootstrap";

const ExpertsTable = ({ experts }) => {

    const expertInfo = ["Username", "Currently working on", "Specializations"]

    return (
        <Table responsive striped bordered hover>
            <thead>
                <tr>{expertInfo.map((label, idx) =>
                    <th key={`expert-label-${idx}`}>{label}</th>)}
                </tr>
            </thead>
            <tbody>
                {experts.map(expert => (
                    <tr key={`expert-${expert.id}`}>
                        <td className='w-25'>{expert.username}</td>
                        <td className='w-25'>{expert.workingOn} <small>{" ticket(s)"}</small></td>
                        <td className='w-50'>
                            {expert.specializations.map(spec => spec.name).join(", ")}
                        </td>
                    </tr>
                ))}
            </tbody>
        </Table>
    )
}

export default ExpertsTable