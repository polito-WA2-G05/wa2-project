// Imports
import { useState } from "react"

const useSessionStorage = () => {
    const getSession = () => {
        return JSON.parse(sessionStorage.getItem('session'))
    }

    const [session, setSession] = useState(getSession())

    const saveSession = (session) => {
        sessionStorage.setItem('session', JSON.stringify(session))
        setSession(session)
    }

    const deleteSession = () => {
        sessionStorage.removeItem('session')
        setSession(null)
    }

    return { setSession: saveSession, deleteSession, session }
}

export default useSessionStorage