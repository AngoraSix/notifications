package com.angorasix.notifications.presentation.handler

import com.angorasix.commons.domain.A6Contributor
import com.angorasix.commons.infrastructure.constants.AngoraSixInfrastructure
import com.angorasix.commons.presentation.dto.BulkPatch
import com.angorasix.commons.presentation.handler.convertToDto
import com.angorasix.commons.reactive.presentation.error.resolveBadRequest
import com.angorasix.notifications.application.NotificationService
import com.angorasix.notifications.domain.notification.I18nText
import com.angorasix.notifications.domain.notification.Notification
import com.angorasix.notifications.infrastructure.config.configurationproperty.api.ApiConfigs
import com.angorasix.notifications.infrastructure.config.i18n.I18nConfigValues
import com.angorasix.notifications.infrastructure.persistence.repository.NotificationListProjection
import com.angorasix.notifications.infrastructure.queryfilters.ListNotificationsFilter
import com.angorasix.notifications.presentation.dto.A6PageMetadata
import com.angorasix.notifications.presentation.dto.I18TextDto
import com.angorasix.notifications.presentation.dto.NotificationDto
import com.angorasix.notifications.presentation.dto.SupportedBulkPatchOperations
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import org.springframework.hateoas.MediaTypes
import org.springframework.hateoas.PagedModel
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.noContent
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.sse

/**
 * Notifications Handler (Controller) containing all handler functions related to Notifications endpoints.
 *
 * @author rozagerardo
 */
class NotificationHandler(
    private val service: NotificationService,
    private val apiConfigs: ApiConfigs,
    private val i18nConfigValues: I18nConfigValues,
    private val objectMapper: ObjectMapper,
) {
    /**
     * Handler for the List Notifications endpoint, retrieving a Flux including all persisted Notifications.
     *
     * @param request - HTTP `ServerRequest` object
     * @return the `ServerResponse`
     */
    suspend fun listNotifications(request: ServerRequest): ServerResponse {
        val requestingContributor =
            request.attributes()[AngoraSixInfrastructure.REQUEST_ATTRIBUTE_CONTRIBUTOR_KEY]
        return if (requestingContributor is A6Contributor) {
            service
                .findNotifications(
                    ListNotificationsFilter.fromMultiValueMap(
                        request.queryParams(),
                    ),
                    requestingContributor,
                ).convertToDto(apiConfigs, request, i18nConfigValues)
                .let {
                    ok().contentType(MediaTypes.HAL_FORMS_JSON).bodyValueAndAwait(it)
                }
        } else {
            resolveBadRequest("Invalid Contributor", "Contributor")
        }
    }

    /**
     * Handler for the List Notifications endpoint, retrieving a Flux including all persisted Notifications.
     *
     * @param request - HTTP `ServerRequest` object
     * @return the `ServerResponse`
     */
    suspend fun listenNotificationsForContributor(request: ServerRequest): ServerResponse {
        val requestingContributor =
            request.attributes()[AngoraSixInfrastructure.REQUEST_ATTRIBUTE_CONTRIBUTOR_KEY]
        return if (requestingContributor is A6Contributor) {
            service
                .listenNotificationsForContributor(
                    requestingContributor,
                ).map {
                    it?.convertToDto(
                        apiConfigs,
                        request,
                        i18nConfigValues,
                    )
                }.map {
                    objectMapper.writeValueAsString(it)
                }.filterNotNull()
                .let {
                    ok().sse().bodyAndAwait(it)
                }
        } else {
            resolveBadRequest("Invalid Contributor", "Contributor")
        }
    }

    /**
     * Handler for the Dismiss all Contributor Notifications endpoint.
     *
     * @param request - HTTP `ServerRequest` object
     * @return the `ServerResponse`
     */
    suspend fun patchNotifications(request: ServerRequest): ServerResponse {
        val contributor =
            request.attributes()[AngoraSixInfrastructure.REQUEST_ATTRIBUTE_CONTRIBUTOR_KEY]
        val patch = request.awaitBody(BulkPatch::class)
        return if (contributor is A6Contributor) {
            try {
                val modifyOperations =
                    patch.operations.map {
                        it.toBulkModificationStrategy(
                            contributor,
                            SupportedBulkPatchOperations.values().map { it.op }.toList(),
                        )
                    }
                service.bulkModification(contributor, modifyOperations)
                noContent().buildAndAwait()
            } catch (ignore: IllegalArgumentException) {
                resolveBadRequest("Invalid Notification Patch Body", "Notification Patch")
            }
        } else {
            resolveBadRequest("Invalid Contributor", "Contributor")
        }
    }
}

private fun NotificationListProjection.convertToDto(
    apiConfigs: ApiConfigs,
    request: ServerRequest,
    i18nConfigValues: I18nConfigValues,
): PagedModel<NotificationDto> {
    // Fix this when Spring HATEOAS provides consistent support for reactive/coroutines
    val dtoResources = this.data.map { it.convertToDto(apiConfigs, request, i18nConfigValues) }
    val pagedMetadata =
        A6PageMetadata(
            pageSize.toLong(),
            page.toLong(),
            total.toLong(),
            totalToRead.toLong(),
            extraSkip.toLong(),
        )
    val pagedModel =
        if (dtoResources.isEmpty()) {
            PagedModel.empty(pagedMetadata, NotificationDto::class.java)
        } else {
            PagedModel.of(dtoResources, pagedMetadata).withFallbackType(NotificationDto::class.java)
        }
    return pagedModel.resolveHypermedia(
        apiConfigs,
        request,
    )
}

private fun Notification.convertToDto(i18nConfigValues: I18nConfigValues): NotificationDto =
    NotificationDto(
        id,
        targetId,
        targetType,
        objectId,
        objectType,
        topic,
        isUnique,
        title.toDto(i18nConfigValues),
        message.toDto(i18nConfigValues),
        creationInstant,
        media?.convertToDto(),
        alertLevel,
        refUri,
        contextData,
        issuanceInstant,
        needsExplicitDismiss,
        dismissed, // more complex dismissedForUser, maybe at some point
    )

private fun Notification.convertToDto(
    apiConfigs: ApiConfigs,
    request: ServerRequest,
    i18nConfigValues: I18nConfigValues,
): NotificationDto =
    convertToDto(i18nConfigValues).resolveHypermedia(
        apiConfigs,
        request,
    )

private fun I18nText.toDto(i18nValues: I18nConfigValues): I18TextDto {
    val i18nFormatted =
        i18nValues.values[objectType]
            ?.get(i18nKey)
            ?.i18nValues
            ?.map { (locale, valueWithPlaceholder) ->
                val formattedText =
                    placeholderParams?.entries?.fold(valueWithPlaceholder) { accString, (pattern, newReplacement) ->
                        accString.replace(":$pattern", newReplacement)
                    } ?: valueWithPlaceholder
                (locale to formattedText).toEntry()
            }?.associateBy({ it.key }, { it.value }) ?: emptyMap()
    return I18TextDto(i18nFormatted)
}

fun <K, V> Pair<K, V>.toEntry() =
    object : Map.Entry<K, V> {
        override val key: K = first
        override val value: V = second
    }
