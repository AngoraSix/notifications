package com.angorasix.notifications.application

import com.angorasix.commons.domain.SimpleContributor
import com.angorasix.commons.infrastructure.intercommunication.messaging.dto.A6InfraMessageDto
import com.angorasix.notifications.application.strategies.determineHandlingStrategy
import com.angorasix.notifications.domain.notification.Notification
import com.angorasix.notifications.domain.notification.NotificationRepository
import com.angorasix.notifications.infrastructure.config.i18n.I18nConfigKeys
import com.angorasix.notifications.infrastructure.persistence.repository.NotificationListProjection
import com.angorasix.notifications.infrastructure.queryfilters.ListNotificationsFilter
import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service providing functionality for Notifications.
 *
 * @author rozagerardo
 */
class NotificationService(
    private val repository: NotificationRepository,
    private val i18nKeys: I18nConfigKeys,
) {

    /**
     * Method to retrieve a collection of [Notification]s.
     *
     * @return [Flux] of [Notification]
     */
    suspend fun findNotifications(
        filter: ListNotificationsFilter,
        contributor: SimpleContributor,
    ): NotificationListProjection = repository.findUsingFilter(filter, contributor)

    /**
     * Method to dismiss all notifications for a contributor.
     *
     * @param newNotification [Notification] to persist
     * @return a [Mono] with the persisted [Notification]
     */
    suspend fun dismissNotifications(
        filter: ListNotificationsFilter,
        contributor: SimpleContributor,
    ) = repository.dismissForContributorUsingFilter(filter, contributor)

    fun processMessage(
        message: A6InfraMessageDto,
    ): Flow<Notification>? =
        determineHandlingStrategy(message)?.processMessage(message, repository, i18nKeys)

    fun listenNotificationsForContributor(contributor: SimpleContributor): Flow<Notification?> =
        repository.listenNotificationsForContributor(contributor)
}
