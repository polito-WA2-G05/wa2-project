package it.polito.wa2.g05.server

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig: WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.setApplicationDestinationPrefixes("/app") // client sends here
        registry.enableSimpleBroker("/queue") // all topic
        registry.setUserDestinationPrefix("/queue") // specified user topic
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .withSockJS()
    //è opzionale ma non tutti i browser supportano le WS
    // e quindi per supportare le ws in tutti i brows usiamo questa libreria
    // 3 opzioni -> supportato (ws), non supportato (streaming), polling
    }
}