// Imports
import {createContext, useContext, useEffect, useRef, useState} from "react"

// Components
import {Loader} from "@components/layout"

// Hooks
import {useNotification} from "@hooks"

// Services
import api from "@services"

// Contexts
import {SessionContext} from "@contexts"

import SockJs from "sockjs-client"
import {over} from "stompjs"
import {SOCKET_URL} from "@services/config"

const NotificationContext = createContext(null)

const NotificationProvider = ({children}) => {
    const [notifications, setNotifications] = useState([])
    const [connected, setConnected] = useState(false)
    const [loading, setLoading] = useState(true)

    const {session} = useContext(SessionContext)
    const notify = useNotification()

    const stompClient = useRef(null)

    useEffect(() => {
        if (loading && session)
            api.utils.getNotifications()
                .then(notifications => setNotifications(notifications))
                .catch(err => notify.error(err.detail ?? err))
                .finally(() => setLoading(false))
    }, [loading, session]) // eslint-disable-line

    useEffect(() => {
        if (session && !connected && !stompClient.current) {
            const sock = new SockJs(SOCKET_URL)
            stompClient.current = over(sock)
            stompClient.current.connect({}, onConnect, onError)
        }
    }, [session]) // eslint-disable-line

    const deleteNotification = (id) => {
        api.utils.deleteNotification(id)
            .catch(err => notify.error(err.detail ?? err))
            .finally(() => setLoading(true));
    }

    const onConnect = () => {
        setConnected(true)
        stompClient.current.subscribe(`/queue/${session?.details.uuid}/notifications`, onMessageReceive)
    }

    const onError = (err) => {
        notify.error(err)
    }

    const onMessageReceive = ({body}) => {
        const notification = JSON.parse(body)
        setNotifications(old => [...old, notification])
        notify.info(notification.text)
    }

    const sendNotification = (message, isForManagers = false) => {
        const path = isForManagers ? "/app/notifications/managers" : "/app/notifications"
        stompClient.current.send(path, {}, JSON.stringify({...message, timestamp: new Date()}))
    }

    if (!session) return children

    if (session && connected)
        return <NotificationContext.Provider value={{notifications, sendNotification, deleteNotification}}>
            {children}
        </NotificationContext.Provider>

    return <Loader/>
}

export {NotificationProvider, NotificationContext}