package com.angorasix.notifications.messaging.handler

import com.angorasix.commons.infrastructure.intercommunication.messaging.dto.A6InfraMessageDto
import com.angorasix.notifications.application.NotificationService

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
class NotificationMessagingHandler(private val notificationService: NotificationService) {

    suspend fun handleMessage(message: A6InfraMessageDto) {
        notificationService.processMessage(message)
    }
}

