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
    val dismissed: Boolean? = null,
    val page: Int = 0,
    val pageSize: Int = 20,
    val sort: List<Pair<String, SortOrder>> =
        listOf(
            "dismissed" to SortOrder.DESC,
            "instantOfCreation" to SortOrder.DESC,
        ),
    val extraSkip: Int = 0,
) {
    companion object {
        fun fromMultiValueMap(multiMap: MultiValueMap<String, String>): ListNotificationsFilter =
            ListNotificationsFilter(
                multiMap.getFirst(ApiConstants.IDS_QUERY_PARAM)?.split(","),
                multiMap.getFirst(ApiConstants.DISMISSED_QUERY_PARAM)?.toBoolean(),
                multiMap.getFirst(ApiConstants.PAGE_QUERY_PARAM)?.toInt()
                    ?: ApiConstants.PAGE_DEFAULT_VALUE,
                multiMap.getFirst(ApiConstants.PAGESIZE_QUERY_PARAM)?.toInt()
                    ?: ApiConstants.PAGESIZE_DEFAULT_VALUE,
                multiMap
                    .getFirst(ApiConstants.SORT_QUERY_PARAM)
                    ?.split(",")
                    ?.map {
                        val order = it.substring(0, 1)
                        val field = it.substring(1)
                        val sortOrder =
                            if (order == ">") {
                                SortOrder.DESC
                            } else {
                                SortOrder.ASC
                            }
                        field to sortOrder
                    }
                    ?: listOf(
                        "dismissed" to SortOrder.DESC,
                        "instantOfCreation" to SortOrder.DESC,
                    ),
                multiMap.getFirst(ApiConstants.EXTRA_SKIP_QUERY_PARAM)?.toInt() ?: 0,
            )
    }
}

enum class SortOrder(
    val value: String,
) {
    ASC("asc"),
    DESC("desc"),
}
