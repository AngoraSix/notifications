package com.angorasix.notifications.domain.notification

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import java.net.URI
import java.time.Instant

/**
 * Notification Aggregate Root.
 *
 * A Notification targeted to someone (or something?).
 *
 * @author rozagerardo
 */
data class Notification @PersistenceCreator private constructor(
    @field:Id val id: String?,
    val targetId: String,
    val targetType: String,
    val objectId: String,
    val objectType: String,
    val topic: String,
    val isUnique: Boolean,
    val title: String,
    val message: String,
    val instantOfCreation: Instant, // instant in which the notification was created (internal)
    val media: NotificationMedia?,
    val alertLevel: AlertLevel,
    val refUri: URI?,
//    val actions: String?, // NOT TODO, will use HATEOAS for this?
    val contextData: Any?,
    val instantOfIssue: Instant, // instant in which the (potentially external) event occurred
    val needsExplicitDismiss: Boolean,
    val dismissed: Boolean,
) {

    /**
     * The final constructor that sets all initial fields.
     */
    constructor(
        targetId: String,
        targetType: String,
        objectId: String,
        objectType: String,
        topic: String,
        isUnique: Boolean,
        title: String,
        message: String,
        instantOfCreation: Instant,
        media: NotificationMedia?,
        alertLevel: AlertLevel = AlertLevel.INFO,
        refUri: URI?,
        contextData: Any?,
        instantOfIssue: Instant = Instant.now(),
        needsExplicitDismiss: Boolean = false,
        dismissed: Boolean = false,
    ) : this(
        null,
        targetId,
        targetType,
        objectId,
        objectType,
        topic,
        isUnique,
        title,
        message,
        instantOfCreation,
        media,
        alertLevel,
        refUri,
        contextData,
        instantOfIssue,
        needsExplicitDismiss,
        dismissed,
    )
}
