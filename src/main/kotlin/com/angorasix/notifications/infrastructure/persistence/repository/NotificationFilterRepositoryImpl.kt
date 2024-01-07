package com.angorasix.notifications.infrastructure.persistence.repository

import com.angorasix.commons.domain.SimpleContributor
import com.angorasix.commons.infrastructure.intercommunication.A6DomainResource
import com.angorasix.notifications.domain.notification.Notification
import com.angorasix.notifications.infrastructure.queryfilters.ListNotificationsFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
class NotificationFilterRepositoryImpl(val mongoOps: ReactiveMongoOperations) :
    NotificationFilterRepository {

    override fun findUsingFilter(
        filter: ListNotificationsFilter,
        simpleContributor: SimpleContributor,
    ): Flow<Notification> {
        return mongoOps.find(filter.toQuery(simpleContributor), Notification::class.java).asFlow()
    }

    override suspend fun dismissForContributorUsingFilter(
        filter: ListNotificationsFilter,
        simpleContributor: SimpleContributor,
    ) {
        mongoOps.updateMulti(
            filter.toDismissQuery(simpleContributor),
            dismissUpdate(),
            Notification::class.java,
        ).awaitFirstOrNull()
    }
}

private fun ListNotificationsFilter.toQuery(simpleContributor: SimpleContributor): Query {
    val query = Query()
    query.addCriteria(
        Criteria().andOperator(
            where("targetType").`is`(A6DomainResource.CONTRIBUTOR),
            where("targetId").`is`(simpleContributor.contributorId),
        ),
    )
    dismissed?.let { query.addCriteria(where("dismissed").`is`(dismissed)) }

    return query
}

private fun ListNotificationsFilter.toDismissQuery(simpleContributor: SimpleContributor): Query {
    val query = Query()
    query.addCriteria(
        Criteria().andOperator(
            where("targetType").`is`(A6DomainResource.CONTRIBUTOR),
            where("targetId").`is`(simpleContributor.contributorId),
        ),
    )
    query.addCriteria(where("dismissed").`is`(false))
    query.addCriteria(where("needsExplicitDismiss").`is`(false))
    ids?.let { query.addCriteria(where("_id").`in`(it)) }
    return query
}

private fun dismissUpdate() = Update().set("dismissed", true)
