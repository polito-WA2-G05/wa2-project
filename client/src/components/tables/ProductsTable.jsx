// Imports
import React from 'react'
import { Table, Button } from "react-bootstrap";
import { useNavigate } from 'react-router-dom';

const ProductsTable = ({ products, purchased }) => {
    const navigate = useNavigate()

    const productInfo = ["Ean", "Name", "Brand"].concat(purchased ? ["Purchased At", "Actions"] : []);

    const navigateToProduct = (ean) => {
        !purchased && navigate(`/products/${ean}`)
    }

    const handleNewTicket = (ean) => {
        navigate(`/tickets/new`, {
            state: { ean }
        })
    }

    return (
        <Table responsive striped bordered hover>
            <thead>
                <tr>
                    {productInfo.map((label, idx) =>
                        <th key={`product-label-${idx}`}>{label}</th>)}
                </tr>
            </thead>
            <tbody>
                {products.map(product => {
                    const onProductClick = () => navigateToProduct(product.ean)
                    const onNewTicketClick = () => handleNewTicket(product.ean)

                    return <tr key={`product-${product.ean}`}>
                        <td className={`${!purchased && "link-primary w-25"}`} onClick={onProductClick} style={{ cursor: "pointer" }}>
                            {product.ean}
                        </td>
                        <td className={`${purchased && "w-25"}`}>{product.name}</td>
                        <td className={""}>{product.brand || "-"}</td>
                        {purchased &&
                            <>
                                <td className='w-25'>{new Date(product.purchasedAt).toLocaleString()}</td>
                                <td className='w-25'>
                                    <Button onClick={onNewTicketClick}>
                                        New Ticket
                                    </Button>
                                </td>
                            </>
                        }
                    </tr>
                })}
            </tbody>
        </Table>
    )
}

export default ProductsTable