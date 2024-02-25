package com.angorasix.notifications.infrastructure.persistence.repository

import com.angorasix.commons.domain.SimpleContributor
import com.angorasix.commons.infrastructure.intercommunication.A6DomainResource
import com.angorasix.notifications.domain.notification.Notification
import com.angorasix.notifications.infrastructure.queryfilters.ListNotificationsFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrDefault
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.aggregation.Aggregation.count
import org.springframework.data.mongodb.core.aggregation.Aggregation.facet
import org.springframework.data.mongodb.core.aggregation.Aggregation.limit
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.project
import org.springframework.data.mongodb.core.aggregation.Aggregation.skip
import org.springframework.data.mongodb.core.aggregation.ArrayOperators
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

    override suspend fun findUsingFilter(
        filter: ListNotificationsFilter,
        simpleContributor: SimpleContributor,
    ): NotificationListProjection {
        println("WHATA")
        println(filter)
//        return mongoOps.aggregate(Aggregates.facet(match()))

//        long total = getCount(<your property name>, <your property value>);
//
        var aggregation = newAggregation(
            match(
                where("targetType").`is`(A6DomainResource.CONTRIBUTOR.value).and("targetId")
                    .`is`(simpleContributor.contributorId),
            ),
            facet(
                skip((filter.page * filter.pageSize).toLong()),
                limit(filter.pageSize.toLong()),
            ).`as`("data")
                .and(count().`as`("total")).`as`("total")
                .and(match(where("dismissed").`is`(false)), count().`as`("totalToRead"))
                .`as`("totalToRead"),
            project("data")
                .and(ArrayOperators.ArrayElemAt.arrayOf("total").elementAt(0)).`as`("total")
                .and(ArrayOperators.ArrayElemAt.arrayOf("totalToRead").elementAt(0))
                .`as`("totalToRead"),
            project("data")
                .and("total.total").`as`("total")
                .and("totalToRead.totalToRead").`as`("totalToRead"),
        )
//            facet(count().`as`("total")).`as`("total"),
//            facet(match(where("dismissed").`is`(false)), count().`as`("total")).`as`("totalToRead"),
//        )

//
        val result: NotificationListProjection = mongoOps.aggregate(
            aggregation,
            Notification::class.java,
            NotificationListProjection::class.java,
        ).awaitFirstOrDefault(
            NotificationListProjection(emptyList(), 0, 0, filter.page, filter.pageSize),
        )
        result.page = filter.page
        result.pageSize = filter.pageSize
        return result

        //.getMappedResults();
//
//        return new PageImpl<>(results, pageable, total);
//        mongoOps.aggre
//        return mongoOps.find(filter.toQuery(simpleContributor), Notification::class.java).asFlow()
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

    override fun listenNotificationsForContributor(simpleContributor: SimpleContributor): Flow<Notification?> =
        mongoOps.changeStream(Notification::class.java).filter(
            where("targetType").`is`(A6DomainResource.CONTRIBUTOR.value).and("targetId")
                .`is`(simpleContributor.contributorId),
        ).listen().asFlow().map { it.body }
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
