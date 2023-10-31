// Import
import { useContext, useEffect } from "react"
import { Outlet, useNavigate } from "react-router-dom";

// Context
import { useNotification } from "@hooks";
import { SessionContext } from "@contexts";

const RoleRoute = ({ roles }) => {
    const { session } = useContext(SessionContext)

    const navigate = useNavigate();
    const notify = useNotification()

    useEffect(() => {
        if (!roles.includes(session.details.authorities[0])) {
            notify.error(`You have not permissions to navigate there.`)
            navigate('/tickets/search', { replace: true });
        }
    }, []); // eslint-disable-line

    if (roles.includes(session.details.authorities[0])) return <Outlet />
}

export default RoleRoute;
