import { useState } from "react"

const useSessionStorage = () => {
    const getSession = () => {
        return JSON.parse(sessionStorage.getItem('session'))
    }

    const [session, setSession] = useState(getSession())
    const [loggedIn, setLoggedIn] = useState(session ? true : false)

    const saveSession = (session) => {
        sessionStorage.setItem('session', JSON.stringify(session))
        setSession(session)
        setLoggedIn(true)
    }

    const deleteSession = () => {
        sessionStorage.removeItem('session')
        setSession(null)
        setLoggedIn(false)
    }

    return { setSession: saveSession, deleteSession, session, loggedIn }
}

export default useSessionStorage