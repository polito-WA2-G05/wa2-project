// Imports
import {useState} from 'react';
import {useNavigate, Link} from "react-router-dom";

// Components
import {Button, Form} from 'react-bootstrap';

// Services
import api from '../services/api';

// Hooks
import useNotification from "../hooks/useNotification";

const SearchProfile = () => {
    const navigate = useNavigate()
    const notify = useNotification()
    const [email, setEmail] = useState("")

    function handleSubmit(e) {
        e.preventDefault()
        api.getProfileByEmail(email)
            .then((data) => navigate(`profiles/${data.email}`, {
                replace: true,
                state: {profile: data}
            }))
            .catch((error) => notify.error(error.detail))
    }

    return (
        <>
            <div className="text-center">
                <h1 className="mb-5 fs-1 fw-bold">Search profile</h1>
            </div>
            <Form className={"d-flex flex-column align-items-center justify-content-center"}>
                <div className={"d-flex align-items-end justify-content-center"}>
                    <Form.Group controlId="search-email">
                        <Form.Label>Search by email</Form.Label>
                        <Form.Control type="text" placeholder="Enter Email"
                                      value={email} onChange={(e) => setEmail(e.target.value)}/>
                    </Form.Group>
                    <Button className={"ms-3"} variant="primary" type="submit" onClick={handleSubmit} disabled={!email}>
                        Search
                    </Button>
                    <Link to={"/profiles/add"} className={"ms-3"}>
                        <Button variant="primary" type="submit">
                            Add new profile
                        </Button>
                    </Link>
                </div>
            </Form>
        </>
    );
};

export default SearchProfile;
