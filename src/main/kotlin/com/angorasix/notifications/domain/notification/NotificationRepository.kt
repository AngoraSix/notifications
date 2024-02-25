package com.angorasix.notifications.domain.notification

import com.angorasix.notifications.infrastructure.persistence.repository.NotificationFilterRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

/**
 *
 *
 * @author rozagerardo
 */
interface NotificationRepository :
    CoroutineCrudRepository<Notification, String>,
    NotificationFilterRepository
