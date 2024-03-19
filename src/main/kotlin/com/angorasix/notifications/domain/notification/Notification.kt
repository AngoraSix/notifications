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
    val title: I18nText,
    val message: I18nText,
    val instantOfCreation: Instant = Instant.now(), // instant in which the notification was created (internal)
    val media: NotificationMedia?,
    val alertLevel: AlertLevel,
    val refUri: URI?,
    val contextData: Any?,
    val instantOfIssue: Instant = Instant.now(), // instant in which the (potentially external) event occurred
    val needsExplicitDismiss: Boolean,
    val dismissed: Boolean, // in future: + dismissedUsers (List) and dismissedForUser (Boolean) in Dto from these two.
) {

    /**
     * The final constructor that sets all initial fields.
     */
    private constructor(
        targetId: String,
        targetType: String,
        objectId: String,
        objectType: String,
        topic: String,
        isUnique: Boolean,
        title: I18nText,
        message: I18nText,
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
    data class Builder(
        var targetId: String,
        var targetType: String,
        var objectId: String,
        var objectType: String,
        var topic: String,
        var isUnique: Boolean = false,
        var title: I18nText? = null,
        var message: I18nText? = null,
        var instantOfCreation: Instant = Instant.now(),
        var media: NotificationMedia? = null,
        var alertLevel: AlertLevel = AlertLevel.INFO,
        var refUri: URI? = null,
        var contextData: Any? = null,
        var instantOfIssue: Instant = Instant.now(),
        var needsExplicitDismiss: Boolean = false,
    ) {

        var ignoreNotification: Boolean = false
        fun media(media: NotificationMedia) = apply {
            this.media = media
        }

        fun unique(isUnique: Boolean) = apply {
            this.isUnique = isUnique
        }

        fun title(title: I18nText) = apply {
            this.title = title
        }

        fun message(message: I18nText) = apply {
            this.message = message
        }

        fun text(title: I18nText, message: I18nText) = apply {
            this.title = title
            this.message = message
        }

        fun alertLeveL(alertLevel: AlertLevel) = apply {
            this.alertLevel = alertLevel
        }

        fun instantOfIssue(instantOfIssue: Instant) = apply {
            this.instantOfIssue = instantOfIssue
        }

        fun needsExplicitDismiss(needsExplicitDismiss: Boolean) = apply {
            this.needsExplicitDismiss = needsExplicitDismiss
        }

        fun ignoreNotification(ignoreNotification: Boolean) = apply {
            this.ignoreNotification = ignoreNotification
        }

        fun build(): Notification? {
            return if (!this.ignoreNotification && this.title != null && this.message != null) {
                Notification(
                    this.targetId,
                    this.targetType,
                    this.objectId,
                    this.objectType,
                    this.topic,
                    this.isUnique,
                    this.title!!,
                    this.message!!,
                    this.instantOfCreation,
                    this.media,
                    this.alertLevel,
                    this.refUri,
                    this.contextData,
                    this.instantOfIssue,
                    this.needsExplicitDismiss,
                    false,
                )
            } else {
                null
            }
        }
    }
}
