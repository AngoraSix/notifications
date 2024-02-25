package com.angorasix.notifications.presentation.handler

import com.angorasix.commons.domain.SimpleContributor
import com.angorasix.commons.infrastructure.constants.AngoraSixInfrastructure
import com.angorasix.commons.presentation.handler.convertToDto
import com.angorasix.commons.reactive.presentation.error.resolveBadRequest
import com.angorasix.notifications.application.NotificationService
import com.angorasix.notifications.domain.notification.I18nText
import com.angorasix.notifications.domain.notification.Notification
import com.angorasix.notifications.infrastructure.config.configurationproperty.api.ApiConfigs
import com.angorasix.notifications.infrastructure.config.i18n.I18nConfigValues
import com.angorasix.notifications.infrastructure.constants.ApiConstants
import com.angorasix.notifications.infrastructure.persistence.repository.NotificationListProjection
import com.angorasix.notifications.infrastructure.queryfilters.ListNotificationsFilter
import com.angorasix.notifications.presentation.dto.A6PageMetadata
import com.angorasix.notifications.presentation.dto.I18TextDto
import com.angorasix.notifications.presentation.dto.NotificationDto
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.Link
import org.springframework.hateoas.MediaTypes
import org.springframework.hateoas.PagedModel
import org.springframework.hateoas.mediatype.Affordances
import org.springframework.http.HttpMethod
import org.springframework.util.MultiValueMapAdapter
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.ServerResponse.status
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.sse
import org.springframework.web.util.UriComponentsBuilder

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
        return if (requestingContributor is SimpleContributor) {
            service.findNotifications(
                ListNotificationsFilter.fromMultiValueMap(
                    request.queryParams(),
                ),
                requestingContributor,
            ).convertToDto(apiConfigs, request, i18nConfigValues)
//                .map {
//                it.convertToDto(
////                    requestingContributor,
//                    apiConfigs,
//                    request,
//                    i18nConfigValues,
//                )
//            }
                .let {
                    ok().contentType(MediaTypes.HAL_FORMS_JSON).bodyValueAndAwait(it)
                }
        } else {
            resolveBadRequest("Invalid Contributor Header", "Contributor Header")
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
        return if (requestingContributor is SimpleContributor) {
            service.listenNotificationsForContributor(
                requestingContributor,
            ).map {
                it?.convertToDto(
//                    requestingContributor,
                    apiConfigs,
                    request,
                    i18nConfigValues,
                )
            }
                .map {
                    objectMapper.writeValueAsString(it)
//                    ServerSentEvent.builder(it).build()
                }
                .filterNotNull().let {
                    ok().sse().bodyAndAwait(it)
//                    ok().contentType(MediaType.APPLICATION_NDJSON).bodyAndAwait(it)
                }
        } else {
            resolveBadRequest("Invalid Contributor Header", "Contributor Header")
        }
    }

    /**
     * Handler for the Dismiss all Contributor Notifications endpoint.
     *
     * @param request - HTTP `ServerRequest` object
     * @return the `ServerResponse`
     */
    suspend fun dismissAllNotifications(request: ServerRequest): ServerResponse {
        return status(501).contentType(MediaTypes.HAL_FORMS_JSON).buildAndAwait()
    }

    /**
     * Handler for the Dismiss a single Contributor Notifications endpoint.
     *
     * @param request - HTTP `ServerRequest` object
     * @return the `ServerResponse`
     */
    suspend fun dismissSingleNotification(request: ServerRequest): ServerResponse {
        return status(501).contentType(MediaTypes.HAL_FORMS_JSON).buildAndAwait()
    }
}

private fun NotificationListProjection.convertToDto(
    apiConfigs: ApiConfigs,
    request: ServerRequest,
    i18nConfigValues: I18nConfigValues,
): PagedModel<NotificationDto> {
    println("GERRRRCHHH")
    println(i18nConfigValues.values.toString())
//    // Fix this when Spring HATEOAS provides consistent support for reactive/coroutines
    val dtoResources = this.data.map { it.convertToDto(apiConfigs, request, i18nConfigValues) }
    val pagedMetadata =
        A6PageMetadata(pageSize.toLong(), page.toLong(), total.toLong(), totalToRead.toLong())
    val pagedModel = if (dtoResources.isNullOrEmpty()) {
        PagedModel.empty(pagedMetadata, NotificationDto::class.java)
    } else {
        PagedModel.of(dtoResources, pagedMetadata).withFallbackType(NotificationDto::class.java)
    }
//    val nextPage = if ((page * pageSize) >= total) {
//        null
//    } else {
//        page + 1
//    }
    return pagedModel.resolveHypermedia(
        apiConfigs,
        request,
    )
}

private fun Notification.convertToDto(i18nConfigValues: I18nConfigValues): NotificationDto {
    return NotificationDto(
        id,
        targetId,
        targetType,
        objectId,
        objectType,
        topic,
        isUnique,
        title.toDto(i18nConfigValues),
        message.toDto(i18nConfigValues),
        instantOfCreation,
        media?.convertToDto(),
        alertLevel,
        refUri,
        contextData,
        instantOfIssue,
        needsExplicitDismiss,
        dismissed,
    )
}

private fun Notification.convertToDto(
//    simpleContributor: SimpleContributor?,
    apiConfigs: ApiConfigs,
    request: ServerRequest,
    i18nConfigValues: I18nConfigValues,
): NotificationDto = convertToDto(i18nConfigValues).resolveHypermedia(
    apiConfigs,
    request,
) // (simpleContributor, this, apiConfigs, request)

private fun NotificationDto.resolveHypermedia(
//    simpleContributor: SimpleContributor?,
//    notification: Notification,
    apiConfigs: ApiConfigs,
    request: ServerRequest,
): NotificationDto {
    val getSingleRoute =
        apiConfigs.routes.listenNotifications // TODO change this need get single notification
    // self
    val selfLink =
        Link.of(uriBuilder(request).path(getSingleRoute.resolvePath()).build().toUriString())
            .withRel(getSingleRoute.name).expand(id).withSelfRel()
    val selfLinkWithDefaultAffordance =
        Affordances.of(selfLink).afford(HttpMethod.OPTIONS).withName("default").toLink()
    add(selfLinkWithDefaultAffordance)

    // dismiss (for needs explicit dismiss notification)
    if (needsExplicitDismiss && !dismissed) {
        val dismissNotificationRoute = apiConfigs.routes.dismissNotification
        val dismissNotificationLinkName = "dismissNotification"
        val dismissNotificationLink = Link.of(
            uriBuilder(request).path(dismissNotificationRoute.resolvePath()).build()
                .toUriString(),
        ).withTitle(dismissNotificationLinkName).withName(dismissNotificationLinkName)
            .withRel(dismissNotificationLinkName)
        val dismissNotificationAffordanceLink =
            Affordances.of(dismissNotificationLink).afford(dismissNotificationRoute.method)
                .withName(dismissNotificationLinkName).toLink()
        add(dismissNotificationAffordanceLink)
    }

    return this
}

private fun uriBuilder(request: ServerRequest) = request.requestPath().contextPath().let {
    UriComponentsBuilder.fromHttpRequest(request.exchange().request).replacePath(it.toString())
//    ForwardedHeaderUtils.adaptFromForwardedHeaders(
//        request.exchange().request.getURI(),
//        request.exchange().request.getHeaders(),
//    ).replacePath(it.toString()) //
//        .replaceQuery("")
}

private fun I18nText.toDto(i18nValues: I18nConfigValues): I18TextDto {
    val i18nFormatted = i18nValues.values[objectType]?.get(i18nKey)?.i18nValues
        ?.map { (locale, valueWithPlaceholder) ->
            val formattedText =
                placeholderParams?.entries?.fold(valueWithPlaceholder) { accString, (pattern, newReplacement) ->
                    accString.replace(":$pattern", newReplacement)
                } ?: valueWithPlaceholder
            (locale to formattedText).toEntry()
        }
        ?.associateBy({ it.key }, { it.value }) ?: emptyMap()
    return I18TextDto(i18nFormatted)
}

fun <K, V> Pair<K, V>.toEntry() = object : Map.Entry<K, V> {
    override val key: K = first
    override val value: V = second
}

fun PagedModel<NotificationDto>.resolveHypermedia(
    apiConfigs: ApiConfigs,
    request: ServerRequest,
): PagedModel<NotificationDto> {
    val listAllRoute = apiConfigs.routes.listNotifications
    // self
    val selfLink = Link.of(
        uriBuilder(request).path(listAllRoute.resolvePath()).build().toUriString(),
    ).withRel(listAllRoute.name).withSelfRel()
    val selfLinkWithDefaultAffordance =
        Affordances.of(selfLink).afford(HttpMethod.OPTIONS).withName("default").toLink()
    add(selfLinkWithDefaultAffordance)

    // listen
    val listenNotificationsRoute = apiConfigs.routes.listenNotifications
    val listenNotificationsLinkName = "listenNotifications"
    val listenNotificationsLink = Link.of(
        uriBuilder(request).path(listenNotificationsRoute.resolvePath()).replaceQueryParams(null)
            .build()
            .toUriString(),
    ).withTitle(listenNotificationsLinkName).withName(listenNotificationsLinkName)
        .withRel(listenNotificationsLinkName)
    val listenNotificationsAffordanceLink =
        Affordances.of(listenNotificationsLink).afford(listenNotificationsRoute.method)
            .withName(listenNotificationsLinkName).toLink()
    add(listenNotificationsAffordanceLink)

    previousLink.ifPresent {
        val previousAffordanceLink =
            Affordances.of(it).afford(listAllRoute.method)
                .withName(IanaLinkRelations.PREVIOUS_VALUE).toLink()
        add(previousAffordanceLink)
    }

    // has next / next link
    if (metadata?.number?.plus(1)?.compareTo(metadata!!.totalPages) ?: 1 < 0) {
        val loadMoreLinkName = IanaLinkRelations.NEXT_VALUE
        val loadMoreQueryParams = request.queryParams().toMutableMap()
        loadMoreQueryParams.replace(
            ApiConstants.PAGE_QUERY_PARAM.value,
            mutableListOf(metadata?.number?.plus(1).toString()),
        )
        val loadMoreLink = Link.of(
            uriBuilder(request).path(listAllRoute.resolvePath())
                .queryParams(MultiValueMapAdapter(loadMoreQueryParams))
                .build()
                .toUriString(),
        ).withTitle(loadMoreLinkName).withName(loadMoreLinkName)
            .withRel(loadMoreLinkName)
        val loadMoreAffordanceLink =
            Affordances.of(loadMoreLink).afford(listAllRoute.method)
                .withName(loadMoreLinkName).toLink()
        add(loadMoreAffordanceLink)
    }

    // has previous / previous link
    if (metadata?.number?.compareTo(0) ?: -1 > 0) {
        val previousLinkName = IanaLinkRelations.PREVIOUS_VALUE
        val previousQueryParams = request.queryParams().toMutableMap()
        previousQueryParams.replace(
            ApiConstants.PAGE_QUERY_PARAM.value,
            mutableListOf(metadata?.number?.plus(1).toString()),
        )
        val previousLink = Link.of(
            uriBuilder(request).path(listAllRoute.resolvePath())
                .queryParams(MultiValueMapAdapter(previousQueryParams))
                .build()
                .toUriString(),
        ).withTitle(previousLinkName).withName(previousLinkName)
            .withRel(previousLinkName)
        val previousAffordanceLink =
            Affordances.of(previousLink).afford(listAllRoute.method)
                .withName(previousLinkName).toLink()
        add(previousAffordanceLink)
    }
    return this
}
