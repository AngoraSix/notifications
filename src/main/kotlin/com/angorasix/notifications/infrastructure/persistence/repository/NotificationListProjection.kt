package com.angorasix.notifications.infrastructure.persistence.repository

import com.angorasix.notifications.domain.notification.Notification
import com.angorasix.notifications.infrastructure.constants.ApiConstants
import org.springframework.data.annotation.PersistenceCreator

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
data class NotificationListProjection(
    val data: List<Notification> = emptyList(),
    val total: Int = 0,
    val totalToRead: Int = 0,
    var page: Int = 0,
    var pageSize: Int = 20,
    var extraSkip: Int = 0,
) {
    @PersistenceCreator
    constructor(
        data: List<Notification>,
        total: Int = 0,
        totalToRead: Int = 0,
    ) : this(
        data,
        total,
        totalToRead,
        ApiConstants.PAGE_DEFAULT_VALUE,
        ApiConstants.PAGESIZE_DEFAULT_VALUE,
        ApiConstants.EXTRA_SKIP_DEFAULT_VALUE,
    )
}
