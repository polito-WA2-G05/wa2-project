// Imports
import React, {useContext} from 'react'
import {Link, useNavigate} from 'react-router-dom'
import {Dropdown, DropdownButton, Nav, Navbar as NavigationBar} from "react-bootstrap";
import {ImUser} from 'react-icons/im'
import {NotificationCenter} from "@components";
// Services
import api from '@services';

// Hooks
import {useNotification} from '@hooks';

// Contexts
import {SessionContext} from "@contexts";

import {guestNavigation, navigation} from "./navigation";

// Utils

const Navbar = () => {
    const {session, role, deleteSession} = useContext(SessionContext);

    const notify = useNotification();
    const navigate = useNavigate();

    const handleLogout = () => {
        api.auth.logout()
            .then(() => {
                deleteSession();
                navigate('/', {replace: true});
                notify.success("See you soon!");
            })
            .catch((err) => notify.error(err.detail) ?? err)
    }

    return (
        <NavigationBar className={"p-4 w-full"} bg="light" variant="dark" sticky="top" style={{zIndex: 999}}>
            <Nav className="d-flex justify-content-between align-items-center w-100">
                <Link to={"/"} className={"text-decoration-none d-flex align-items-center fw-semibold"}>
                    <span>Home</span>
                </Link>
                {!session ? <GuestNavigation/> :
                    <LoggedInNavigation session={session} role={role} onLogout={handleLogout}/>
                }
            </Nav>
        </NavigationBar>
    )
}

const GuestNavigation = () => <div className='d-flex align-items-center'>
    {guestNavigation.map(path =>
        <Link key={`path-to-${path.label}`} to={path.url}
              className={"text-decoration-none d-flex align-items-center fw-semibold mx-4"}>
            <span>{path.label}</span>
        </Link>
    )}
</div>

const LoggedInNavigation = ({session, role, onLogout}) => {
    const navigate = useNavigate();

    return (
    <div className="d-flex">
        <NotificationCenter/>
        <Dropdown className="ms-4">
            <DropdownButton
                variant="primary"
                drop={"start"} title={
                <span className='d-inline-flex align-items-center px-2 fw-semibold'>
                    <ImUser className={"me-2"} size={15}/>
                    <span>{session.info.name ?? session.details.username}</span>
                </span>}
                className='align-items-center d-flex'
            >
                {navigation[role.toLowerCase()].map(group =>
                    <div key={`group-${group.title}`}>
                        <Dropdown.ItemText as={"small"} className='my-2'>
                            {group.title}
                        </Dropdown.ItemText>
                        {group.paths.map(path =>
                            <Dropdown.Item key={`path-to-${path.label}`} as="button" onClick={() => navigate(path.url)}>
                                {path.label}
                            </Dropdown.Item>
                        )}
                        <Dropdown.Divider/>
                    </div>
                )}

                <Dropdown.Item className='text-danger' onClick={onLogout}>
                    Logout
                </Dropdown.Item>
            </DropdownButton>
        </Dropdown>
    </div>
    )
}

export default Navbar