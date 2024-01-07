package com.angorasix.notifications.infrastructure.persistence.repository

import com.angorasix.commons.domain.SimpleContributor
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

    fun findUsingFilter(
        filter: ListNotificationsFilter,
        simpleContributor: SimpleContributor,
    ): Flow<Notification>

    suspend fun dismissForContributorUsingFilter(
        filter: ListNotificationsFilter,
        simpleContributor: SimpleContributor,
    )
}
