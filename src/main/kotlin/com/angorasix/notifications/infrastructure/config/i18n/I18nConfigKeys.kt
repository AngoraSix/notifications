package com.angorasix.notifications.infrastructure.config.i18n

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
@ConfigurationProperties(prefix = "configs.i18n.keys")
data class I18nConfigKeys(
    var clubContributorAddedTitle: String,
    var clubContributorAddedMessage: String,
    var clubMemberAddedTitle: String,
    var clubMemberAddedMessage: String,
)
