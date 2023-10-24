// Import
import { useContext, useEffect } from "react"
import { Outlet, useNavigate } from "react-router-dom";

// Context
import { useNotification } from "@hooks";
import { SessionContext } from "@contexts";

const ProtectedRoute = () => {
    const { session } = useContext(SessionContext)

    const navigate = useNavigate();
    const notify = useNotification()

    useEffect(() => {
        if (!session) {
            notify.error("You should be logged in to navigate there!")
            navigate('/', { replace: true });
        }
    }, []); // eslint-disable-line

    if (session) return <Outlet />
}

export default ProtectedRoute;
