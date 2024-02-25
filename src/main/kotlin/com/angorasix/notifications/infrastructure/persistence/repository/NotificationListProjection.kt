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
) {
    @PersistenceCreator
    constructor(
        data: List<Notification>,
        total: Int = 0, //List<Map<String, Int>?>?,
        totalToRead: Int = 0, //List<Map<String, Int>?>?,
    ) : this(
        data,
        total,
        totalToRead,
//        total?.get(0)?.get("total") ?: 0,
//        totalToRead?.get(0)?.get("totalToRead") ?: 0,
        0,
        20,
    )
}