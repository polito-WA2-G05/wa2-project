// Import
import { useEffect } from "react"
import { Outlet, useNavigate } from "react-router-dom";

// Context
import { useSessionStorage } from "@hooks";

const GuestRoute = () => {
    const { loggedIn } =  useSessionStorage()
    const navigate = useNavigate();

    useEffect(() => {
        if (loggedIn)
            navigate('/me', { replace: true });
    }, []); // eslint-disable-line react-hooks/exhaustive-deps

    if (!loggedIn) return <Outlet />
}

export default GuestRoute;
