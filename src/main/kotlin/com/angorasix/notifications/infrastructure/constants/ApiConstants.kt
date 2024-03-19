package com.angorasix.notifications.infrastructure.constants

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
class ApiConstants private constructor() {
    companion object {
        const val IDS_QUERY_PARAM = "ids"
        const val DISMISSED_QUERY_PARAM = "dismissed"
        const val PAGE_QUERY_PARAM = "page"
        const val PAGE_DEFAULT_VALUE = 0
        const val PAGESIZE_QUERY_PARAM = "pageSize"
        const val PAGESIZE_DEFAULT_VALUE = 20
        const val SORT_QUERY_PARAM = "sort"
        const val EXTRA_SKIP_QUERY_PARAM = "extraSkip"
        const val EXTRA_SKIP_DEFAULT_VALUE = 0
    }
}
