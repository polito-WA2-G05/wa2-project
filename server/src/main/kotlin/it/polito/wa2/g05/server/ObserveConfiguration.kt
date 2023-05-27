package it.polito.wa2.g05.server

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class ObserveConfiguration {
    @Bean
    fun observedAspect(observationRegistry: ObservationRegistry): ObservedAspect =
        ObservedAspect(observationRegistry)
}
