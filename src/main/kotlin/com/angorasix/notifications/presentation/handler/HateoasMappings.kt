package com.angorasix.notifications.presentation.handler

import com.angorasix.notifications.infrastructure.config.configurationproperty.api.ApiConfigs
import com.angorasix.notifications.infrastructure.config.configurationproperty.api.Route
import com.angorasix.notifications.infrastructure.constants.ApiConstants
import com.angorasix.notifications.presentation.dto.NotificationDto
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.Link
import org.springframework.hateoas.PagedModel
import org.springframework.hateoas.mediatype.Affordances
import org.springframework.http.HttpMethod
import org.springframework.util.MultiValueMapAdapter
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.util.UriComponentsBuilder

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
fun NotificationDto.resolveHypermedia(
    apiConfigs: ApiConfigs,
    request: ServerRequest,
): NotificationDto {
    val getSingleRoute =
        apiConfigs.routes.listenNotifications // TODO change this need get single notification?
    // self
    val selfLink =
        Link
            .of(uriBuilder(request).path(getSingleRoute.resolvePath()).build().toUriString())
            .withRel(getSingleRoute.name)
            .expand(id)
            .withSelfRel()
    val selfLinkWithDefaultAffordance =
        Affordances
            .of(selfLink)
            .afford(HttpMethod.OPTIONS)
            .withName("default")
            .toLink()
    add(selfLinkWithDefaultAffordance)

    // dismiss (for needs explicit dismiss notification)
    if (needsExplicitDismiss && !dismissedForUser) {
        val dismissNotificationRoute = apiConfigs.routes.patchNotification
        val dismissNotificationLinkName = "dismissNotification"
        val dismissNotificationLink =
            Link
                .of(
                    uriBuilder(request)
                        .path(dismissNotificationRoute.resolvePath())
                        .build()
                        .toUriString(),
                ).withTitle(dismissNotificationLinkName)
                .withName(dismissNotificationLinkName)
                .withRel(dismissNotificationLinkName)
        val dismissNotificationAffordanceLink =
            Affordances
                .of(dismissNotificationLink)
                .afford(dismissNotificationRoute.method)
                .withName(dismissNotificationLinkName)
                .toLink()
        add(dismissNotificationAffordanceLink)
    }

    return this
}

fun PagedModel<NotificationDto>.resolveHypermedia(
    apiConfigs: ApiConfigs,
    request: ServerRequest,
): PagedModel<NotificationDto> {
    val listAllRoute = apiConfigs.routes.listNotifications
    // self
    val selfLink =
        Link
            .of(
                uriBuilder(request).path(listAllRoute.resolvePath()).build().toUriString(),
            ).withRel(listAllRoute.name)
            .withSelfRel()
    val selfLinkWithDefaultAffordance =
        Affordances
            .of(selfLink)
            .afford(HttpMethod.OPTIONS)
            .withName("default")
            .toLink()
    add(selfLinkWithDefaultAffordance)

    // listen
    addListenLink(apiConfigs, request)

    // has next / next link
    if (metadata?.number?.plus(1)?.compareTo(metadata!!.totalPages) ?: 1 < 0) {
        addNextLink(request, listAllRoute)
    }

    // has previous / previous link
    if (metadata?.number?.compareTo(0) ?: -1 > 0) {
        addPreviousLink(request, listAllRoute)
    }
    return this
}

private fun PagedModel<NotificationDto>.addListenLink(
    apiConfigs: ApiConfigs,
    request: ServerRequest,
) {
    val listenNotificationsRoute = apiConfigs.routes.listenNotifications
    val listenNotificationsLinkName = "listenNotifications"
    val listenNotificationsLink =
        Link
            .of(
                uriBuilder(request)
                    .path(listenNotificationsRoute.resolvePath())
                    .replaceQueryParams(null)
                    .build()
                    .toUriString(),
            ).withTitle(listenNotificationsLinkName)
            .withName(listenNotificationsLinkName)
            .withRel(listenNotificationsLinkName)
    val listenNotificationsAffordanceLink =
        Affordances
            .of(listenNotificationsLink)
            .afford(listenNotificationsRoute.method)
            .withName(listenNotificationsLinkName)
            .toLink()
    add(listenNotificationsAffordanceLink)
}

private fun PagedModel<NotificationDto>.addNextLink(
    request: ServerRequest,
    listAllRoute: Route,
) {
    val loadMoreLinkName = IanaLinkRelations.NEXT_VALUE
    val loadMoreQueryParams = request.queryParams().toMutableMap()
    loadMoreQueryParams.replace(
        ApiConstants.PAGE_QUERY_PARAM,
        mutableListOf(metadata?.number?.plus(1).toString()),
    )
    val loadMoreLink =
        Link
            .of(
                uriBuilder(request)
                    .path(listAllRoute.resolvePath())
                    .queryParams(MultiValueMapAdapter(loadMoreQueryParams))
                    .build()
                    .toUriString(),
            ).withTitle(loadMoreLinkName)
            .withName(loadMoreLinkName)
            .withRel(loadMoreLinkName)
    val loadMoreAffordanceLink =
        Affordances
            .of(loadMoreLink)
            .afford(listAllRoute.method)
            .withName(loadMoreLinkName)
            .toLink()
    add(loadMoreAffordanceLink)
}

private fun PagedModel<NotificationDto>.addPreviousLink(
    request: ServerRequest,
    listAllRoute: Route,
) {
    val previousLinkName = IanaLinkRelations.PREVIOUS_VALUE
    val previousQueryParams = request.queryParams().toMutableMap()
    previousQueryParams.replace(
        ApiConstants.PAGE_QUERY_PARAM,
        mutableListOf(metadata?.number?.plus(1).toString()),
    )
    val previousLink =
        Link
            .of(
                uriBuilder(request)
                    .path(listAllRoute.resolvePath())
                    .queryParams(MultiValueMapAdapter(previousQueryParams))
                    .build()
                    .toUriString(),
            ).withTitle(previousLinkName)
            .withName(previousLinkName)
            .withRel(previousLinkName)
    val previousAffordanceLink =
        Affordances
            .of(previousLink)
            .afford(listAllRoute.method)
            .withName(previousLinkName)
            .toLink()
    add(previousAffordanceLink)
}

private fun uriBuilder(request: ServerRequest) =
    request.requestPath().contextPath().let {
        UriComponentsBuilder.fromHttpRequest(request.exchange().request).replacePath(it.toString())
    }
