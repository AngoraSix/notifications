package com.angorasix.notifications.presentation.dto

import com.angorasix.commons.presentation.dto.A6MediaDto
import com.angorasix.notifications.domain.notification.AlertLevel
import com.fasterxml.jackson.annotation.JsonUnwrapped
import org.springframework.hateoas.PagedModel
import org.springframework.hateoas.RepresentationModel
import java.net.URI
import java.time.Instant

/**
 *
 *
 * @author rozagerardo
 */
data class NotificationDto(
    var id: String? = null,
    val targetId: String? = null,
    val targetType: String? = null,
    val objectId: String? = null,
    val objectType: String? = null,
    val topic: String? = null,
    val isUnique: Boolean? = null,
    val title: I18TextDto? = null,
    val message: I18TextDto? = null,
    val instantOfCreation: Instant? = null,
    val media: A6MediaDto? = null,
    val alertLevel: AlertLevel? = null,
    val refUri: URI? = null,
    val contextData: Any? = null,
    val instantOfIssue: Instant? = null,
    val needsExplicitDismiss: Boolean,
    val dismissed: Boolean,
) : RepresentationModel<NotificationDto>()

data class I18TextDto(@JsonUnwrapped val i18n: Map<String, String>)

class A6PageMetadata(pageSize: Long, page: Long, total: Long, totalToRead: Long, extraSkip: Long) :
    PagedModel.PageMetadata(pageSize, page, total) {
    val totalToRead = totalToRead
    val extraSkip = extraSkip
}
