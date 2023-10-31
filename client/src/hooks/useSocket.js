import {useContext, useEffect, useRef, useState} from "react";

import SockJs from "sockjs-client"
import {over} from "stompjs"

import {SOCKET_URL} from "@services/config"

import {SessionContext} from "@contexts"

import {useNotification} from "@hooks";

const useSocket = (
    {channel, onConnect = () => null, onError = () => null, onMessageReceive = () => null, onSendMessage = () => null}
) => {
    const [messages, setMessages] = useState([])
    const [connected, setConnected] = useState(false)

    const {session} = useContext(SessionContext)
    const notify = useNotification()

    const stompClient = useRef(null)

    useEffect(() => {
        if (session && !connected && !stompClient.current) {
            const sock = new SockJs(SOCKET_URL)
            stompClient.current = over(sock)
            stompClient.current.connect({}, handleConnect, handleError)
        }
    }, [session]) // eslint-disable-line

    const handleConnect = () => {
        setConnected(true)
        stompClient.current.subscribe(`/queue/${session?.details.uuid}/${channel}`, handleMessageReceive)
        onConnect()
    }

    const handleError = (err) => {
        notify.error(err)
        onError()
    }

    const handleMessageReceive = ({body}) => {
        const message = JSON.parse(body)
        setMessages(old => [...old, message])
        onMessageReceive()
    }

    const sendMessage = (message, {includeLocally}) => {
        stompClient.current.send(`/app/${channel}`, {}, JSON.stringify(message))
        if (includeLocally) setMessages(old => [...old, message])
        onSendMessage()
    }

    const disconnect = () => {
        stompClient.current.disconnect(() => {
            setMessages([])
            setConnected(false)
        })
    }

    return {connected, messages, sendMessage, setInitialMessages: (messages) => setMessages(messages), disconnect}
}

export default useSocket