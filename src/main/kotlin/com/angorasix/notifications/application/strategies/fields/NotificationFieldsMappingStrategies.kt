package com.angorasix.notifications.application.strategies.fields

import com.angorasix.commons.domain.A6Media
import com.angorasix.commons.infrastructure.intercommunication.A6InfraTopics
import com.angorasix.commons.infrastructure.intercommunication.messaging.dto.A6InfraMessageDto
import com.angorasix.notifications.domain.notification.I18nText
import com.angorasix.notifications.domain.notification.Notification
import com.angorasix.notifications.domain.notification.NotificationMedia
import com.angorasix.notifications.infrastructure.config.i18n.I18nConfigKeys

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
class AddMemberEventStrategy : NotificationFieldMappingStrategy() {
    override fun internalMapNotificationFields(
        message: A6InfraMessageDto,
        builder: Notification.Builder,
        i18nKeys: I18nConfigKeys,
    ): Notification? {
        val titleI18n: I18nText
        val messageI18n: I18nText
        val clubType = message.messageData["type"]
        if (clubType == "contributor-candidates") {
            titleI18n = I18nText(i18nKeys.clubsContributorAddedTitle, message.targetType.value)
            messageI18n = I18nText(
                i18nKeys.clubsContributorAddedMessage,
                message.targetType.value,
                mapOf(
                    "firstName" to (message.requestingContributor.firstName
                        ?: message.requestingContributor.contributorId),
                ),
            )
        } else {
            titleI18n = I18nText(i18nKeys.clubMemberAddedTitle, message.targetType.value)
            messageI18n = I18nText(
                i18nKeys.clubMemberAddedMessage,
                message.targetType.value,
                mapOf(
                    "clubType" to ((clubType as String?) ?: message.objectId),
                ),
            )
        }
        println("GERRRRRRRRR")
        println(titleI18n)
        println(messageI18n)
        builder.text(titleI18n, messageI18n)
        message.requestingContributor.profileMedia?.let { builder.media(it.toNotificationMedia()) }
        return builder.build()
    }
}

abstract class NotificationFieldMappingStrategy {
    fun mapNotificationFields(
        inputMessage: A6InfraMessageDto,
        i18nKeys: I18nConfigKeys,
    ): Notification? {
        val builder = Notification.Builder(
            inputMessage.targetId,
            inputMessage.targetType.value,
            inputMessage.objectId,
            inputMessage.objectType,
            inputMessage.topic,
        )
        return internalMapNotificationFields(inputMessage, builder, i18nKeys)
    }

    abstract protected fun internalMapNotificationFields(
        inputMessage: A6InfraMessageDto,
        notificationBuilder: Notification.Builder,
        i18nKeys: I18nConfigKeys,
    ): Notification?
}

fun determineFieldsStrategy(message: A6InfraMessageDto): NotificationFieldMappingStrategy? {
    return when (message.topic) {
        A6InfraTopics.ADD_MEMBER.value -> AddMemberEventStrategy()
        else -> null
    }
}

private fun A6Media.toNotificationMedia(): NotificationMedia =
    NotificationMedia(mediaType, url, thumbnailUrl, resourceId)