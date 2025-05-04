package com.angorasix.notifications.presentation.router

import com.angorasix.commons.reactive.presentation.filter.extractRequestingContributor
import com.angorasix.notifications.infrastructure.config.configurationproperty.api.ApiConfigs
import com.angorasix.notifications.presentation.handler.NotificationHandler
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.coRouter

/**
 * Router for all Notifications related endpoints.
 *
 * @author rozagerardo
 */
class NotificationRouter(
    private val handler: NotificationHandler,
    private val apiConfigs: ApiConfigs,
) {
    /**
     * Main RouterFunction configuration for all endpoints related to Notifications.
     *
     * @return the [RouterFunction] with all the routes for Notifications
     */
    fun notificationRouterFunction() =
        coRouter {
            filter { request, next ->
                extractRequestingContributor(
                    request,
                    next,
                )
            }
            apiConfigs.basePaths.notifications.nest {
                apiConfigs.routes.baseListCrudRoute.nest {
                    defineListenNotificationsEndpoint()
                    defineListNotificationsEndpoint()
                    definePatchNotificationsEndpoint()
                }
            }
        }

    private fun CoRouterFunctionDsl.defineListNotificationsEndpoint() {
        method(apiConfigs.routes.listNotifications.method).nest {
            method(apiConfigs.routes.listNotifications.method, handler::listNotifications)
        }
    }

    private fun CoRouterFunctionDsl.definePatchNotificationsEndpoint() {
        method(apiConfigs.routes.patchNotifications.method).nest {
            method(apiConfigs.routes.patchNotifications.method, handler::patchNotifications)
        }
    }

    private fun CoRouterFunctionDsl.defineListenNotificationsEndpoint() {
        method(apiConfigs.routes.listenNotifications.method).nest {
            headers { it.accept().contains(MediaType.TEXT_EVENT_STREAM) }.nest {
                method(
                    apiConfigs.routes.listenNotifications.method,
                    handler::listenNotificationsForContributor,
                )
            }
        }
    }
}
