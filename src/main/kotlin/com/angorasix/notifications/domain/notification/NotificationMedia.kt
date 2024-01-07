package com.angorasix.notifications.domain.notification

import com.angorasix.commons.domain.A6Media

/**
 *
 *
 * @author rozagerardo
 */
class NotificationMedia(
    mediaType: String,
    url: String,
    thumbnailUrl: String,
    resourceId: String,
) : A6Media(mediaType, url, thumbnailUrl, resourceId)
