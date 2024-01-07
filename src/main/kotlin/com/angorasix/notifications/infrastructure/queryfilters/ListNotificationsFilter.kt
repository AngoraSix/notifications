package com.angorasix.notifications.infrastructure.queryfilters

import org.springframework.util.MultiValueMap

/**
 * <p> Classes containing different Request Query Filters.
 * </p>
 *
 * @author rozagerardo
 */
data class ListNotificationsFilter(
    val ids: Collection<String>? = null,
    val dismissed: Boolean?,
) {
    companion object {
        fun fromMultiValueMap(
            multiMap: MultiValueMap<String, String>,
        ): ListNotificationsFilter {
            return ListNotificationsFilter(
                multiMap.getFirst("ids")?.split(","),
                multiMap.getFirst("dismissed")?.toBoolean(),
            )
        }
    }
}
