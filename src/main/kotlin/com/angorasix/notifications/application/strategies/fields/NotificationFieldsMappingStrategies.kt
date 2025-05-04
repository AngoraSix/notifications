package com.angorasix.notifications.application.strategies.fields

import com.angorasix.commons.domain.A6Media
import com.angorasix.commons.infrastructure.intercommunication.A6InfraTopics
import com.angorasix.commons.infrastructure.intercommunication.messaging.A6InfraMessageDto
import com.angorasix.notifications.domain.notification.I18nText
import com.angorasix.notifications.domain.notification.Notification
import com.angorasix.notifications.domain.notification.NotificationMedia
import com.angorasix.notifications.infrastructure.config.i18n.I18nConfigKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
class AddMemberEventStrategy : NotificationFieldMappingStrategy() {
    override fun internalMapNotificationFields(
        message: A6InfraMessageDto<Map<String, Any>>,
        builder: Notification.Builder,
        i18nKeys: I18nConfigKeys,
    ): Flow<Notification>? {
        val titleI18n: I18nText
        val messageI18n: I18nText
        val clubType = message.messageData["type"]
        if (clubType == "CONTRIBUTOR_CANDIDATES") {
            titleI18n = I18nText(i18nKeys.clubContributorAddedTitle, message.objectType)
            messageI18n =
                I18nText(
                    i18nKeys.clubContributorAddedMessage,
                    message.objectType,
                    mapOf(
                        "firstName" to (
                            message.requestingContributor.firstName
                                ?: message.requestingContributor.contributorId
                        ),
                    ),
                )
        } else {
            titleI18n = I18nText(i18nKeys.clubMemberAddedTitle, message.objectType)
            messageI18n =
                I18nText(
                    i18nKeys.clubMemberAddedMessage,
                    message.objectType,
                    mapOf(
                        "clubType" to ((clubType as String?) ?: message.objectId),
                    ),
                )
        }
        builder.text(titleI18n, messageI18n)
        message.requestingContributor.profileMedia?.let { builder.media(it.toNotificationMedia()) }
        return builder.build()?.let { flowOf(it) }
    }
}

abstract class NotificationFieldMappingStrategy {
    fun mapNotificationFields(
        inputMessage: A6InfraMessageDto<Map<String, Any>>,
        i18nKeys: I18nConfigKeys,
    ): Flow<Notification>? {
        val builder =
            Notification.Builder(
                inputMessage.targetId,
                inputMessage.targetType.value,
                inputMessage.objectId,
                inputMessage.objectType,
                inputMessage.topic,
            )
        return internalMapNotificationFields(inputMessage, builder, i18nKeys)
    }

    protected abstract fun internalMapNotificationFields(
        inputMessage: A6InfraMessageDto<Map<String, Any>>,
        notificationBuilder: Notification.Builder,
        i18nKeys: I18nConfigKeys,
    ): Flow<Notification>?
}

fun determineFieldsStrategy(message: A6InfraMessageDto<Map<String, Any>>): NotificationFieldMappingStrategy? =
    when (message.topic) {
        A6InfraTopics.PROJECT_CLUB_MEMBER_JOINED.value -> AddMemberEventStrategy()
        else -> null
    }

private fun A6Media.toNotificationMedia(): NotificationMedia = NotificationMedia(mediaType, url, thumbnailUrl, resourceId)
