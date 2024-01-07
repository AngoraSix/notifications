package com.angorasix.notifications.application

import com.angorasix.commons.domain.SimpleContributor
import com.angorasix.notifications.domain.notification.NotificationRepository
import com.angorasix.notifications.domain.notification.Notification
import com.angorasix.notifications.infrastructure.queryfilters.ListNotificationsFilter
import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service providing functionality for Notifications.
 *
 * @author rozagerardo
 */
class NotificationService(private val repository: NotificationRepository) {

    /**
     * Method to retrieve a collection of [Notification]s.
     *
     * @return [Flux] of [Notification]
     */
    fun findNotifications(
        filter: ListNotificationsFilter,
        contributor: SimpleContributor,
    ): Flow<Notification> = repository.findUsingFilter(filter, contributor)

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
}
