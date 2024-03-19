package com.angorasix.notifications.domain.notification

import com.angorasix.notifications.infrastructure.persistence.repository.NotificationFilterRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

/**
 *
 *
 * @author rozagerardo
 */
interface NotificationRepository :
    CoroutineCrudRepository<Notification, String>,
    NotificationFilterRepository
