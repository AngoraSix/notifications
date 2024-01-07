package com.angorasix.notifications.presentation.dto

import com.angorasix.commons.presentation.dto.A6MediaDto
import com.angorasix.notifications.domain.notification.AlertLevel
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
    val title: String? = null,
    val message: String? = null,
    val instantOfCreation: Instant? = null,
    val media: A6MediaDto? = null,
    val alertLevel: AlertLevel? = null,
    val refUri: URI? = null,
    val contextData: Any? = null,
    val instantOfIssue: Instant? = null,
    val needsExplicitDismiss: Boolean? = null,
    val dismissed: Boolean? = null,
) : RepresentationModel<NotificationDto>()

data class PresentationMediaDto(
    override val mediaType: String,
    override val url: String,
    override val thumbnailUrl: String,
    override val resourceId: String,
) : A6MediaDto(mediaType, url, thumbnailUrl, resourceId)