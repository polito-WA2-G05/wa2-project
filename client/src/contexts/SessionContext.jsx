import {createContext, useEffect, useState} from "react";

import {useNotification} from "@hooks";
import {useNavigate} from "react-router-dom";

const SessionContext = createContext(null)

const SessionProvider = ({children}) => {
    const [reload, setReload] = useState(true)
    const [session, setSession] = useState(null)

    const navigate = useNavigate()
    const notify = useNotification()

    const saveSession = (session) => {
        sessionStorage.setItem('session', JSON.stringify(session))
        setSession(session)
    }

    const deleteSession = () => {
        sessionStorage.removeItem('session')
        setSession(null)
    }

    const onError = (err) => {
        if (err.status === 401) setReload(true)
        else if (err.status === 403) {
            navigate(-1, {replace: true})
        } else notify.error(err.detail ?? err)
    }

    useEffect(() => {
        if (reload)
            setSession(JSON.parse(sessionStorage.getItem('session')))
        setReload(false)
    }, [reload])

    const value = {
        session,
        role: session?.details.authorities[0],
        onError,
        deleteSession,
        saveSession
    }

    if (!reload)
        return <SessionContext.Provider value={value}>
            {children}
        </SessionContext.Provider>
}

export {SessionProvider, SessionContext};