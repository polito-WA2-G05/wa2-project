// Import
import { useEffect, useContext } from "react"
import { Outlet, useNavigate } from "react-router-dom";

// Context
import { SessionContext } from "@contexts";

const GuestRoute = () => {
    const { session } =  useContext(SessionContext)
    const navigate = useNavigate();

    useEffect(() => {
        if (session)
            navigate('/me', { replace: true });
    }, []); // eslint-disable-line

    if (!session) return <Outlet />
}

export default GuestRoute;
