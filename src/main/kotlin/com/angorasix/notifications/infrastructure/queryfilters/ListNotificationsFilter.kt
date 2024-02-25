package com.angorasix.notifications.infrastructure.queryfilters

import com.angorasix.notifications.infrastructure.constants.ApiConstants
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
    val page: Int = 0,
    val pageSize: Int = 20,
    val sort: List<Pair<String, SortOrder>> = listOf(
        "dismissed" to SortOrder.DESC,
        "instantOfCreation" to SortOrder.DESC,
    ),
) {
    companion object {
        fun fromMultiValueMap(
            multiMap: MultiValueMap<String, String>,
        ): ListNotificationsFilter {
            println("FILTERRR")
            println(multiMap)
            return ListNotificationsFilter(
                multiMap.getFirst(ApiConstants.IDS_QUERY_PARAM.value)?.split(","),
                multiMap.getFirst(ApiConstants.DISMISSED_QUERY_PARAM.value)?.toBoolean(),
                multiMap.getFirst(ApiConstants.PAGE_QUERY_PARAM.value)?.toInt() ?: 0,
                multiMap.getFirst(ApiConstants.PAGESIZE_QUERY_PARAM.value)?.toInt() ?: 20,
                multiMap.getFirst(ApiConstants.SORT_QUERY_PARAM.value)?.split(",")?.map {
                    val sortRaw = it.split("+", "-")
                    val sortOrder = if (sortRaw[0] === "+") {
                        SortOrder.ASC
                    } else {
                        SortOrder.DESC
                    }
                    if (sortRaw.size === 2) (sortRaw[1] to sortOrder) else sortRaw[0] to sortOrder
                }
                    ?: listOf(
                        "dismissed" to SortOrder.DESC,
                        "instantOfCreation" to SortOrder.DESC,
                    ),
            )
        }
    }
}

enum class SortOrder(val value: String) {
    ASC("asc"),
    DESC("desc"),
}
