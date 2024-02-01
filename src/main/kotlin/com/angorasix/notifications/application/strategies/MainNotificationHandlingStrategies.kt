package com.angorasix.notifications.application.strategies

import com.angorasix.commons.infrastructure.intercommunication.A6DomainResource
import com.angorasix.commons.infrastructure.intercommunication.messaging.dto.A6InfraMessageDto
import com.angorasix.notifications.application.strategies.fields.determineFieldsStrategy
import com.angorasix.notifications.domain.notification.Notification
import com.angorasix.notifications.domain.notification.NotificationRepository
import com.angorasix.notifications.infrastructure.config.i18n.I18nConfigKeys

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
class MessageMapperStrategies {

}

interface MainNotificationHandlingStrategy {
    suspend fun processMessage(
        message: A6InfraMessageDto,
        notificationRepository: NotificationRepository,
        i18nKeys: I18nConfigKeys,
    ): Notification?
}

class ContributorEventMapperStrategy : MainNotificationHandlingStrategy {
    override suspend fun processMessage(
        message: A6InfraMessageDto,
        notificationRepository: NotificationRepository,
        i18nKeys: I18nConfigKeys,
    ): Notification? {
        val strategy = determineFieldsStrategy(message)
        return strategy?.mapNotificationFields(message, i18nKeys)
            ?.let { notificationRepository.save(it) }
    }
}

fun determineHandlingStrategy(message: A6InfraMessageDto): MainNotificationHandlingStrategy? {
    return when (message.targetType) {
        A6DomainResource.CONTRIBUTOR -> ContributorEventMapperStrategy()
        else -> null
    }
}