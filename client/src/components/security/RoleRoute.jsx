// Import
import { useEffect, useState } from "react"
import { Outlet, useNavigate } from "react-router-dom";
import { Spinner } from "react-bootstrap";

// Context
import { useNotification, useSessionStorage } from "@hooks";

const RoleRoute = ({ roles }) => {
    const [loading, setLoading] = useState(true)
    const { session } = useSessionStorage()
    const navigate = useNavigate();
    const notify = useNotification()

    useEffect(() => {
        if (!roles.includes(session.details.authorities[0])) {
            notify.error(`You have not permissions to navigate there.`)
            navigate('/tickets/search', { replace: true });
        } else { setLoading(false) }
    }, []); // eslint-disable-line react-hooks/exhaustive-deps

    if (!loading && roles.includes(session.details.authorities[0])) return <Outlet />

    return <div className="d-flex justify-content-center align-items-center w-100">
        <Spinner animation='border' size='xl' as='span' role='status' aria-hidden='true' className='me-2' />
        <h2>Loading...</h2>
    </div>
}

export default RoleRoute;
