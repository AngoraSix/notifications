package com.angorasix.notifications.messaging.router

import com.angorasix.commons.infrastructure.intercommunication.messaging.A6InfraMessageDto
import com.angorasix.notifications.messaging.handler.NotificationMessagingHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * <p>
 *     spring-cloud-streams is not prepared to handle Kotlin DSL beans: https://github.com/spring-cloud/spring-cloud-stream/issues/2025
 * </p>
 *
 * @author rozagerardo
 */
@Configuration
class NotificationMessagingRouter(
    val handler: NotificationMessagingHandler,
) {
    @Bean
    fun notifications(): (A6InfraMessageDto<Map<String, Any>>) -> Unit = { handler.handleMessage(it) }
}
