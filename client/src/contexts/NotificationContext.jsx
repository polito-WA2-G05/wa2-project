// Imports
import { createContext, useContext, useEffect, useRef, useState } from "react"

// Components
import { Loader } from "@components/layout"

// Hooks
import { useNotification } from "@hooks"

// Services
import api from "@services"

// Contexts
import { SessionContext } from "@contexts"

import SockJs from "sockjs-client"
import { over } from "stompjs"
import { SOCKET_URL } from "@services/config"

const NotificationContext = createContext(null)

const NotificationProvider = ({ children }) => {
    const [notifications, setNotifications] = useState([])
    const [connected, setConnected] = useState(false)
    const [loading, setLoading] = useState(false)

    const { session } = useContext(SessionContext)
    const notify = useNotification()

    const stompClient = useRef(null)

    useEffect(() => {
        if (session) setLoading(true)
    }, [session])

    useEffect(() => {
        if (loading && session)
            api.utils.getNotifications()
                .then(notifications => setNotifications(notifications))
                .catch(err => notify.error(err.detail ?? err))
                .finally(() => setLoading(false))
    }, [session, loading]) // eslint-disable-line

    useEffect(() => {
        if (session && !connected && !stompClient.current) {
            const sock = new SockJs(SOCKET_URL)
            stompClient.current = over(sock)
            stompClient.current.connect({}, onConnect, onError)
        }
    }, [session]) // eslint-disable-line

    const deleteNotification = (id) => {
        api.utils.deleteNotification(id)
            .then(notifications => setNotifications(notifications))
            .catch(err => notify.error(err.detail ?? err))
    }

    const onConnect = () => {
        setConnected(true)
        stompClient.current.subscribe(`/queue/${session?.details.uuid}/notifications`, onMessageReceive)
    }

    const onError = (err) => {
        notify.error(err)
    }

    const onMessageReceive = ({ body }) => {
        const notification = JSON.parse(body)
        setNotifications(old => [...old, notification])
        notify.info(notification.text)
    }

    const sendNotification = (message, isForManagers = false) => {
        const path = isForManagers ? "/app/notifications/managers" : "/app/notifications"
        stompClient.current.send(path, {}, JSON.stringify({ ...message, timestamp: new Date() }))
    }

    const disconnect = () => {
        stompClient.current.disconnect(() => {
            setNotifications([])
            setConnected(false)
            stompClient.current = null
        })
    }

    if (!loading)
        return <NotificationContext.Provider value={{ notifications, sendNotification, deleteNotification, disconnect }}>
            {children}
        </NotificationContext.Provider>

    return <Loader />
}

export { NotificationProvider, NotificationContext }