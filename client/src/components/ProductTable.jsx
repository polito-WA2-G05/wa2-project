// Imports
import React from 'react'
import {Col, Table} from "react-bootstrap";

const productInfo = ["Ean", "Name", "Brand"]

const ProductTable = ({products}) => {
    return (
        <Col xs={12}>
            <Table striped bordered hover>
                <thead>
                <tr>{productInfo.map((label, idx) => <th key={idx}>{label}</th>)}</tr>
                </thead>
                <tbody>
                {products.map((product, idx) => (
                    <tr key={idx}>
                        <td className={"w-25"}>{product.ean}</td>
                        <td>{product.name}</td>
                        <td className={"w-25"}>{product.brand}</td>
                    </tr>
                ))}
                </tbody>
            </Table>
        </Col>
    )
}

export default ProductTable