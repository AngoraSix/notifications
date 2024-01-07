package com.angorasix.notifications.messaging.handler

import com.angorasix.commons.infrastructure.intercommunication.messaging.A6InfraMessageDto
import org.springframework.context.annotation.Bean
import org.springframework.messaging.Message
import java.util.function.Consumer


//fun messagingFn(): Consumer<String> {

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
//@Bean("notifications")
//fun notifications(): (String) -> Unit {
//    return { println("Received: ${it}") }
////    return Consumer<Message<A6InfraMessageDto>> { message -> println("Received: ${message}")}//.messageData} ${message.objectId} ${message.objectType} ${message.targetType} ${message.targetId} ${message}") }
//}
fun notifications(): Consumer<Message<A6InfraMessageDto>> {
    return Consumer<Message<A6InfraMessageDto>> { message -> println("Received: ${message}")}//.messageData} ${message.objectId} ${message.objectType} ${message.targetType} ${message.targetId} ${message}") }
}
//    return Consumer<String> { message -> println("Received: ${message}") }
//}