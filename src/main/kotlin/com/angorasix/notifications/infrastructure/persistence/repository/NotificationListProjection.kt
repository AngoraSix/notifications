package com.angorasix.notifications.infrastructure.persistence.repository

import com.angorasix.notifications.domain.notification.Notification
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
        0,
        20,
        0,
    )
}
