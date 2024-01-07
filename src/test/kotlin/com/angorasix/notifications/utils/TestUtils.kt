package com.angorasix.notifications.utils

import java.util.*

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
fun mockRequestingContributorHeader(): String {
    val requestingContributorJson = """
        {
          "contributorId": "mockedContributorId1"
        }
    """.trimIndent()
    return Base64.getUrlEncoder().encodeToString(requestingContributorJson.toByteArray())
}
