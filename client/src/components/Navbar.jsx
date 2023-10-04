// Imports
import React from 'react'
import {Navbar as NavigationBar, Nav, Button} from "react-bootstrap";
import {Link} from 'react-router-dom'
import {useLocation} from "react-router-dom";

// Styles
import {ImHome} from 'react-icons/im'

const Navbar = () => {
    const location = useLocation();

    return (
        <NavigationBar className={"p-4 w-full"} bg="light" variant="dark" sticky="top">
            <Nav className="d-flex justify-content-between align-items-center w-100">
                <Link to={"/"} className={"text-decoration-none d-flex align-items-center fw-semibold"}>
                    <ImHome className={"me-2"}/>
                    <span>Home</span>
                </Link>
                <div>
                    <Link to="/products">
                        <Button className="me-3 fw-semibold">
                            <p className="my-auto">Show all products</p>
                        </Button>
                    </Link>

                    {location.pathname !== '' &&
                    <Link to={'/login'}>
                        <Button className="fw-semibold">
                            <p className="my-auto">Login</p>
                        </Button>
                    </Link>
                    }
                </div>
            </Nav>
        </NavigationBar>
    )
}

export default Navbar