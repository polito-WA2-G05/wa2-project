// Import
import { useEffect } from "react"
import { Outlet, useNavigate } from "react-router-dom";

// Context
import { useNotification, useSessionStorage } from "@hooks";

const ProtectedRoute = () => {
    const { loggedIn } = useSessionStorage()
    const navigate = useNavigate();
    const notify = useNotification()

    useEffect(() => {
        if (!loggedIn) {
            notify.error("You should be logged in to navigate there!")
            navigate('/', { replace: true });
        }
    }, []); // eslint-disable-line react-hooks/exhaustive-deps

    if (loggedIn) return <Outlet />
}

export default ProtectedRoute;
