package com.angorasix.notifications.infrastructure.config.i18n

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
@ConfigurationProperties(prefix = "i18n")
class I18nConfigValues(
    values: Map<String, List<I18nValue>>,
) {
    var values: Map<String, Map<String, I18nValue>> =
        values.mapValues { targetTypeEntry -> targetTypeEntry.value.associateBy { i18nValue -> i18nValue.i18nKey } }
}

data class I18nValue(
    var i18nKey: String,
    var i18nValues: Map<String, String>,
)
