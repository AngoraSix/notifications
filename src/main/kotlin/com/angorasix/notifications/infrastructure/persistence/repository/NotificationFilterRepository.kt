package com.angorasix.notifications.infrastructure.persistence.repository

import com.angorasix.commons.domain.A6Contributor
import com.angorasix.notifications.domain.notification.Notification
import com.angorasix.notifications.infrastructure.queryfilters.ListNotificationsFilter
import kotlinx.coroutines.flow.Flow

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
interface NotificationFilterRepository {
    suspend fun findUsingFilter(
        filter: ListNotificationsFilter,
        simpleContributor: A6Contributor,
    ): NotificationListProjection

    suspend fun dismissForContributorUsingFilter(
        filter: ListNotificationsFilter,
        simpleContributor: A6Contributor,
    )

    fun listenNotificationsForContributor(simpleContributor: A6Contributor): Flow<Notification?>
}
