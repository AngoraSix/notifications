package com.angorasix.notifications.messaging.handler

import com.angorasix.commons.infrastructure.intercommunication.messaging.dto.A6InfraMessageDto
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
@Configuration // spring-cloud-streams is not prepared to handle Kotlin DSL beans: https://github.com/spring-cloud/spring-cloud-stream/issues/2025
class NotificationMessagingHandler {

    @Bean
    fun notifications(): (A6InfraMessageDto) -> Unit =
        { message -> println("Received: $message") }
}
