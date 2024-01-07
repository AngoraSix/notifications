package com.angorasix.notifications.presentation.router

import com.angorasix.commons.reactive.presentation.filter.extractRequestingContributor
import com.angorasix.notifications.infrastructure.config.configurationproperty.api.ApiConfigs
import com.angorasix.notifications.presentation.handler.NotificationHandler
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.coRouter

/**
 * Router for all Project related endpoints.
 *
 * @author rozagerardo
 */
class NotificationRouter(
    private val handler: NotificationHandler,
    private val apiConfigs: ApiConfigs,
) {

    /**
     * Main RouterFunction configuration for all endpoints related to Projects.
     *
     * @return the [RouterFunction] with all the routes for Projects
     */
    fun projectRouterFunction() = coRouter {
        filter { request, next ->
            extractRequestingContributor(
                request,
                next,
            )
        }
        apiConfigs.basePaths.notifications.nest {
            apiConfigs.routes.baseListCrudRoute.nest {
                defineListNotificationsEndpoint()
            }
        }
    }

    private fun CoRouterFunctionDsl.defineListNotificationsEndpoint() {
        method(apiConfigs.routes.listNotifications.method).nest {
            method(apiConfigs.routes.listNotifications.method, handler::listNotifications)
        }
    }
}
