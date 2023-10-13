// Imports
import React from 'react'
import { Col, Table } from "react-bootstrap";

const ProductsTable = ({ products }) => {
    const productInfo = ["Ean", "Name", "Brand"]

    return (
        <Col xs={12}>
            <Table responsive striped bordered hover>
                <thead>
                    <tr>{productInfo.map((label, idx) => <th key={`product-label-${idx}`}>{label}</th>)}</tr>
                </thead>
                <tbody>
                    {products.map((product, idx) => (
                        <tr key={`product-${idx}`}>
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

export default ProductsTable