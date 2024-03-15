package com.angorasix.notifications.messaging.handler

import com.angorasix.commons.infrastructure.intercommunication.messaging.dto.A6InfraMessageDto
import com.angorasix.notifications.application.NotificationService
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.runBlocking

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
class NotificationMessagingHandler(private val notificationService: NotificationService) {
    fun handleMessage(message: A6InfraMessageDto) = runBlocking {
        notificationService.processMessage(message)?.launchIn(this)
    }
}
