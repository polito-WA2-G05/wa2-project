// Imports
import {Col} from "react-bootstrap";
import {useLocation, useNavigate} from "react-router-dom";


// Components
import {CreateTicketForm} from "@components/forms";
import {useEffect} from "react";

// Components
const CreateTicket = () => {
    const location = useLocation()
    const navigate = useNavigate()

    const ean = location?.state?.ean

    useEffect(() => {
        if (!ean) {
            navigate("/me/purchases", {replace: true})
        }
    }, []) // eslint-disable-line

    if (ean)
        return (
            <Col xs={12} lg={5}>
                <div className="my-5 text-center">
                    <h5>Product #{ean}</h5>
                    <h1 className="fw-bold">Create a new ticket</h1>
                </div>
                <CreateTicketForm ean={ean}/>
            </Col>
        );
};

export default CreateTicket;
