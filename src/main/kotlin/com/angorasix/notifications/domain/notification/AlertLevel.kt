package com.angorasix.notifications.domain.notification

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
enum class AlertLevel(val code: String) {
    INFO("info"),
    ERROR("error"),
    WARN("warn"),
    SUCCESS("success"),
    NONE("none"),
}