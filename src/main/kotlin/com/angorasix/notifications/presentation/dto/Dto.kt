package com.angorasix.notifications.presentation.dto

import com.angorasix.commons.domain.SimpleContributor
import com.angorasix.commons.presentation.dto.A6MediaDto
import com.angorasix.commons.presentation.dto.BulkPatchOperation
import com.angorasix.commons.presentation.dto.BulkPatchOperationSpec
import com.angorasix.notifications.domain.notification.AlertLevel
import com.angorasix.notifications.infrastructure.constants.BulkDomainModificationConstants
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
    val dismissedForUser: Boolean,
) : RepresentationModel<NotificationDto>()

data class I18TextDto(@JsonUnwrapped val i18n: Map<String, String>)

class A6PageMetadata(pageSize: Long, page: Long, total: Long, totalToRead: Long, extraSkip: Long) :
    PagedModel.PageMetadata(pageSize, page, total) {
    val totalToRead = totalToRead
    val extraSkip = extraSkip
}


enum class SupportedBulkPatchOperations(val op: BulkPatchOperationSpec) {
    DISMISS(
        object : BulkPatchOperationSpec {
            override fun supportsPatchOperation(operation: BulkPatchOperation): Boolean =
                operation.op == "replace" && operation.path == "/dismissed" && (operation.value?.booleanValue() == true)

            override fun mapToStrategyId(
                contributor: SimpleContributor,
                operation: BulkPatchOperation,
            ): String {
                return BulkDomainModificationConstants.DISMISS_FOR_CONTRIBUTOR_STRATEGY.value;
            }
        },
    )
}
