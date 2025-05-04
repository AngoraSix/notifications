package com.angorasix.notifications.application.strategies

import com.angorasix.commons.infrastructure.intercommunication.A6DomainResource
import com.angorasix.commons.infrastructure.intercommunication.messaging.A6InfraMessageDto
import com.angorasix.notifications.application.strategies.fields.determineFieldsStrategy
import com.angorasix.notifications.domain.notification.Notification
import com.angorasix.notifications.domain.notification.NotificationRepository
import com.angorasix.notifications.infrastructure.config.i18n.I18nConfigKeys
import kotlinx.coroutines.flow.Flow

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
class MessageMapperStrategies

interface MainNotificationHandlingStrategy {
    fun processMessage(
        message: A6InfraMessageDto<Map<String, Any>>,
        notificationRepository: NotificationRepository,
        i18nKeys: I18nConfigKeys,
    ): Flow<Notification>?
}

class ContributorEventMapperStrategy : MainNotificationHandlingStrategy {
    override fun processMessage(
        message: A6InfraMessageDto<Map<String, Any>>,
        notificationRepository: NotificationRepository,
        i18nKeys: I18nConfigKeys,
    ): Flow<Notification>? {
        val strategy = determineFieldsStrategy(message)
        return strategy
            ?.mapNotificationFields(message, i18nKeys)
            ?.let { notificationRepository.saveAll(it) }
    }
}

fun determineHandlingStrategy(message: A6InfraMessageDto<Map<String, Any>>): MainNotificationHandlingStrategy? =
    when (message.targetType) {
        A6DomainResource.CONTRIBUTOR -> ContributorEventMapperStrategy()
        else -> null
    }
