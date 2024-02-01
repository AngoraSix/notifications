package com.angorasix.notifications.domain.notification

/**
 *
 *
 * @author rozagerardo
 */
data class I18nText(
    val i18nKey: String,
    val targetType: String,
    val placeholderParams: Map<String, String>? = null,
)
