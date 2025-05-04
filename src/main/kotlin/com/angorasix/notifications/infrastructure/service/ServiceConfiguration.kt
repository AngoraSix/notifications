package com.angorasix.notifications.infrastructure.service

import com.angorasix.notifications.application.NotificationService
import com.angorasix.notifications.domain.notification.NotificationRepository
import com.angorasix.notifications.infrastructure.config.configurationproperty.api.ApiConfigs
import com.angorasix.notifications.infrastructure.config.i18n.I18nConfigKeys
import com.angorasix.notifications.infrastructure.config.i18n.I18nConfigValues
import com.angorasix.notifications.messaging.handler.NotificationMessagingHandler
import com.angorasix.notifications.presentation.handler.NotificationHandler
import com.angorasix.notifications.presentation.router.NotificationRouter
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceConfiguration {
    @Bean
    fun notificationService(
        repository: NotificationRepository,
        i18nKeys: I18nConfigKeys,
    ) = NotificationService(repository, i18nKeys)

    @Bean
    fun notificationHandler(
        service: NotificationService,
        apiConfigs: ApiConfigs,
        i18nConfigValues: I18nConfigValues,
        objectMapper: ObjectMapper,
    ) = NotificationHandler(service, apiConfigs, i18nConfigValues, objectMapper)

    @Bean
    fun notificationRouter(
        handler: NotificationHandler,
        apiConfigs: ApiConfigs,
    ) = NotificationRouter(handler, apiConfigs).notificationRouterFunction()

    @Bean
    fun notificationMessagingHandler(notificationService: NotificationService) = NotificationMessagingHandler(notificationService)
}
