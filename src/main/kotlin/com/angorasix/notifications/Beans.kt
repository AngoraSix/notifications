package com.angorasix.notifications

import com.angorasix.notifications.application.NotificationService
import com.angorasix.notifications.infrastructure.persistence.converter.ZonedDateTimeConvertersUtils
import com.angorasix.notifications.infrastructure.security.NotificationSecurityConfiguration
import com.angorasix.notifications.messaging.handler.notifications
import com.angorasix.notifications.presentation.handler.NotificationHandler
import com.angorasix.notifications.presentation.router.NotificationRouter
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans
import org.springframework.data.mongodb.core.convert.MongoCustomConversions

val beans = beans {
    bean {
        MongoCustomConversions(
            listOf(
                ref<ZonedDateTimeConvertersUtils.ZonedDateTimeReaderConverter>(),
                ref<ZonedDateTimeConvertersUtils.ZonedDateTimeWritingConverter>(),
            ),
        )
    }
    bean {
        NotificationSecurityConfiguration().springSecurityFilterChain(ref())
    }
    bean<NotificationService>()
    bean<NotificationHandler>()
    bean {
        NotificationRouter(ref(), ref()).projectRouterFunction()
    }
    bean(name = "notifications") { // Specifying bean name to reference binding
        notifications()
    }
    //    bean<(String) -> Unit>(name = "notifications") { // Specifying bean name to reference binding
//        notifications()
//        { println("Received: ${it}") }
//    }Ï
}

class BeansInitializer : ApplicationContextInitializer<GenericApplicationContext> {
    override fun initialize(context: GenericApplicationContext) = beans.initialize(context)
}
