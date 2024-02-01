package com.angorasix.notifications.infrastructure.config.i18n

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
@ConfigurationProperties(prefix = "i18n")
data class I18nConfigValues(
    var values: Map<String, Map<String, Map<String, String>>>,
)
