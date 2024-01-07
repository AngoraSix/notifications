package com.angorasix.notifications.presentation.handler

import com.angorasix.commons.domain.SimpleContributor
import com.angorasix.commons.infrastructure.constants.AngoraSixInfrastructure
import com.angorasix.commons.presentation.dto.A6MediaDto
import com.angorasix.commons.presentation.handler.convertToDto
import com.angorasix.commons.reactive.presentation.error.resolveBadRequest
import com.angorasix.notifications.domain.notification.NotificationMedia
import com.angorasix.notifications.application.NotificationService
import com.angorasix.notifications.domain.notification.Notification
import com.angorasix.notifications.infrastructure.config.configurationproperty.api.ApiConfigs
import com.angorasix.notifications.infrastructure.queryfilters.ListNotificationsFilter
import com.angorasix.notifications.presentation.dto.NotificationDto
import kotlinx.coroutines.flow.map
import org.springframework.hateoas.Link
import org.springframework.hateoas.MediaTypes
import org.springframework.hateoas.mediatype.Affordances
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.util.UriComponentsBuilder

/**
 * Project Handler (Controller) containing all handler functions related to Project endpoints.
 *
 * @author rozagerardo
 */
class NotificationHandler(
    private val service: NotificationService,
    private val apiConfigs: ApiConfigs,
) {

    /**
     * Handler for the List Notifications endpoint, retrieving a Flux including all persisted Notifications.
     *
     * @param request - HTTP `ServerRequest` object
     * @return the `ServerResponse`
     */
    suspend fun listNotifications(@Suppress("UNUSED_PARAMETER") request: ServerRequest): ServerResponse {
        val requestingContributor =
            request.attributes()[AngoraSixInfrastructure.REQUEST_ATTRIBUTE_CONTRIBUTOR_KEY]
        return if (requestingContributor is SimpleContributor) {
            service.findNotifications(
                ListNotificationsFilter.fromMultiValueMap(
                    request.queryParams(),
                ),
                requestingContributor,
            ).map {
                it.convertToDto(
                    requestingContributor,
                    apiConfigs,
                    request,
                )
            }.let {
                ok().contentType(MediaTypes.HAL_FORMS_JSON).bodyAndAwait(it)
            }
        } else {
            resolveBadRequest("Invalid Contributor Header", "Contributor Header")
        }
    }
}

private fun Notification.convertToDto(): NotificationDto {
    return NotificationDto(
        id,
        targetId,
        targetType,
        objectId,
        objectType,
        topic,
        isUnique,
        title,
        message,
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


private fun A6MediaDto.convertToDomain(): NotificationMedia {
    return NotificationMedia(
        mediaType,
        url,
        thumbnailUrl,
        resourceId,
    )
}


private fun Notification.convertToDto(
    simpleContributor: SimpleContributor?,
    apiConfigs: ApiConfigs,
    request: ServerRequest,
): NotificationDto = convertToDto().resolveHypermedia(simpleContributor, this, apiConfigs, request)

//private fun NotificationDto.convertToDomain(
//    contributorId: String,
//    admins: Set<SimpleContributor>,
//): Notification {
//    return Notification(
//        targetId,
//        targetType,
//        objectId,
//        objectType,
//        topic,
//        isUnique,
//        title,
//        message,
//        instantOfCreation,
//        media?.convertToDomain(),
//        alertLevel,
//        refUri,
//        contextData,
//        instantOfIssue,
//        needsExplicitDismiss,
//        dismissed,
//    )
//}

private fun NotificationDto.resolveHypermedia(
    simpleContributor: SimpleContributor?,
    notification: Notification,
    apiConfigs: ApiConfigs,
    request: ServerRequest,
): NotificationDto {
    val getSingleRoute = apiConfigs.routes.listNotifications //TODO change this need get single notification
    // self
    val selfLink =
        Link.of(uriBuilder(request).path(getSingleRoute.resolvePath()).build().toUriString())
            .withRel(getSingleRoute.name).expand(id).withSelfRel()
    val selfLinkWithDefaultAffordance =
        Affordances.of(selfLink).afford(HttpMethod.OPTIONS).withName("default").toLink()
    add(selfLinkWithDefaultAffordance)

//    // edit Project
//    if (simpleContributor != null) {
//        if (notification.isAdministeredBy(simpleContributor)) {
//            val editProjectRoute = apiConfigs.routes.updateProject
//            val editProjectLink = Link.of(
//                uriBuilder(request).path(editProjectRoute.resolvePath()).build().toUriString(),
//            ).withTitle(editProjectRoute.name).withName(editProjectRoute.name)
//                .withRel(editProjectRoute.name).expand(id)
//            val editProjectAffordanceLink = Affordances.of(editProjectLink).afford(HttpMethod.PUT)
//                .withName(editProjectRoute.name).toLink()
//            add(editProjectAffordanceLink)
//        }
//    }
    return this
}

private fun uriBuilder(request: ServerRequest) = request.requestPath().contextPath().let {
    UriComponentsBuilder.fromHttpRequest(request.exchange().request).replacePath(it.toString()) //
        .replaceQuery("")
}
