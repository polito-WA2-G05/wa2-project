// Imports
import React from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Navbar as NavigationBar, Nav, Dropdown, DropdownButton } from "react-bootstrap";
import { ImList2, ImSearch, ImUser, ImEnter } from 'react-icons/im'

// Services
import api from '@services';

// Hooks
import { useNotification, useSessionStorage } from '@hooks';

const Navbar = () => {
    const { session, loggedIn, deleteSession } = useSessionStorage();

    const notify = useNotification();
    const navigate = useNavigate();

    const handleLogout = () => {
        api.auth.logout()
            .then(() => {
                deleteSession();
                notify.success("See you soon!");
                navigate('/', { replace: true });
            })
            .catch((err) => notify.error(err.details) ?? err)
    }

    return (
        <NavigationBar className={"p-4 w-full"} bg="light" variant="dark" sticky="top">
            <Nav className="d-flex justify-content-between align-items-center w-100">
                <Link to={"/"} className={"text-decoration-none d-flex align-items-center fw-semibold"}>
                    <ImSearch className={"me-2"} size={20} />
                    <span>Search product</span>
                </Link>
                <div className='d-flex align-items-center'>
                    {!loggedIn &&
                        <>
                            <Link to="/products" className={"text-decoration-none d-flex align-items-center fw-semibold"}>
                                <ImList2 className={"me-2"} size={15} />
                                <p className="my-auto">All products</p>
                            </Link>

                            <Link to={'/login'} className={"text-decoration-none d-flex align-items-center mx-4 fw-semibold"}>
                                <ImEnter className={"me-2"} size={20} />
                                <p className="my-auto">Login</p>
                            </Link>
                        </>
                    }
                    {loggedIn &&
                        <Dropdown>
                            <DropdownButton
                                variant="primary"
                                drop={"start"}
                                title={
                                    <span className='d-inline-flex align-items-center px-2'>
                                        <ImUser className={"me-2"} size={15} />
                                        <span>{session.info.name ?? session.details.username}</span>
                                    </span>
                                }
                                className='align-items-center d-flex'
                            >
                                <Dropdown.Item as="button" onClick={() => navigate('/products', { replace: true })}>All products</Dropdown.Item>
                                <Dropdown.Item as="button" onClick={() => navigate('/tickets', { replace: true })}>All Ticket</Dropdown.Item>
                                <Dropdown.Item as="button" onClick={() => navigate('/tickets/search', { replace: true })}>Search Ticket</Dropdown.Item>
                                <Dropdown.Divider />
                                <Dropdown.ItemText as={"small"} className='mb-2'>
                                    {`${session.details.authorities[0]} Menu`}
                                </Dropdown.ItemText>
                                {
                                    ["Customer", "Expert"].includes(session.details.authorities[0]) &&

                                    <Dropdown.Item as="button" onClick={() => navigate('/me', { replace: true })}>Profile</Dropdown.Item>
                                }
                                {
                                    session.details.authorities.includes("Customer") &&
                                    <>
                                        <Dropdown.Item as="button" onClick={() => navigate('/tickets/new', { replace: true })}>New Ticket</Dropdown.Item>
                                        <Dropdown.Item as="button" onClick={() => navigate('/tickets', { replace: true })}>My Tickets</Dropdown.Item>
                                    </>
                                }
                                {
                                    session.details.authorities.includes("Manager") &&
                                    <Dropdown.Item as="button" onClick={() => navigate('/manager/create-expert', { replace: true })}>Create expert</Dropdown.Item>
                                }
                                <Dropdown.Item as="button" className='text-danger' onClick={() => handleLogout()}>Logout</Dropdown.Item>
                            </DropdownButton>
                        </Dropdown>
                    }
                </div>
            </Nav>
        </NavigationBar >
    )
}

export default Navbar