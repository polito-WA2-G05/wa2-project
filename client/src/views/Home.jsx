// Imports
import React, {useState} from 'react';
import {Button, Form} from 'react-bootstrap';
import {useNavigate} from "react-router-dom";

// Services
import api from '../services/api';

// Hooks
import useNotification from "../hooks/useNotification";

const Home = () => {
    const navigate = useNavigate()
    const notify = useNotification()
    const [ean, setEan] = useState("")

    function handleSubmit(e) {
        e.preventDefault()
        api.getProductByEAN(ean)
            .then((data) => navigate(`/products/${data.ean}`, {
                replace: true,
                state: {product: data}
            }))
            .catch((error) => notify.error(error.detail))
    }

    return (
        <>
            <h1 className="mb-5 fs-1 fw-bold text-center">Search your product</h1>
            <Form onSubmit={handleSubmit} className={"d-flex align-items-end justify-content-center"}>
                <Form.Group controlId="search-ean">
                    <Form.Label>Search by EAN</Form.Label>
                    <Form.Control type="text" placeholder="Enter EAN"
                                  value={ean} onChange={(e) => setEan(e.target.value)}/>
                </Form.Group>
                <Button className={"ms-3"} variant="primary" type="submit" disabled={!ean}>
                    Search
                </Button>
            </Form>
        </>
    );
};

export default Home;
