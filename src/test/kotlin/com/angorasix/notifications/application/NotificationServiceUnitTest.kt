//package com.angorasix.notifications.application
//
//import com.angorasix.commons.domain.SimpleContributor
//import com.angorasix.notifications.domain.project.Attribute
//import com.angorasix.notifications.domain.project.Notification
//import com.angorasix.notifications.domain.project.ProjectRepository
//import com.angorasix.notifications.infrastructure.queryfilters.ListNotificationsFilter
//import io.mockk.Runs
//import io.mockk.coEvery
//import io.mockk.coVerify
//import io.mockk.coVerifyAll
//import io.mockk.confirmVerified
//import io.mockk.every
//import io.mockk.impl.annotations.MockK
//import io.mockk.junit5.MockKExtension
//import io.mockk.just
//import io.mockk.mockk
//import io.mockk.verifyAll
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.flowOf
//import kotlinx.coroutines.test.runTest
//import org.assertj.core.api.AssertionsForClassTypes.assertThat
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith
//import java.time.ZoneId
//
//@ExtendWith(MockKExtension::class)
//@ExperimentalCoroutinesApi
//class NotificationServiceUnitTest {
//    private lateinit var service: NotificationService
//
//    @MockK
//    private lateinit var repository: ProjectRepository
//
//    @BeforeEach
//    fun init() {
//        service = NotificationService(repository)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun `given existing projects - when request find projects - then receive projects`() =
//        runTest {
//            val mockedNotification = Notification(
//                "mockedProjectName",
//                "creator_id",
//                setOf(SimpleContributor("creator_id", emptySet())),
//                ZoneId.systemDefault(),
//            )
//            val filter = ListNotificationsFilter()
//            coEvery { repository.findUsingFilter(filter, null) } returns flowOf(mockedNotification)
//
//            val outputProjects = service.findNotifications(filter, null)
//
//            outputProjects.collect {
//                assertThat<Notification>(it).isSameAs(mockedNotification)
//            }
//            coVerify { repository.findUsingFilter(filter, null) }
//        }
//
//    @Test
//    @Throws(Exception::class)
//    fun givenExistingProject_whenFindSingleProjects_thenServiceRetrievesMonoWithProject() =
//        runTest {
//            val mockedProjectId = "id1"
//            val mockedNotification = Notification(
//                "mockedProjectName",
//                "creator_id",
//                setOf(SimpleContributor("creator_id", emptySet())),
//                ZoneId.systemDefault(),
//            )
//            coEvery {
//                repository.findByIdForContributor(
//                    ListNotificationsFilter(listOf(mockedProjectId)),
//                    null,
//                )
//            } returns mockedNotification
//            val outputProject = service.findSingleProject(mockedProjectId, null)
//            assertThat(outputProject).isSameAs(mockedNotification)
//            coVerify {
//                repository.findByIdForContributor(
//                    ListNotificationsFilter(listOf(mockedProjectId)),
//                    null,
//                )
//            }
//        }
//
//    @Test
//    @Throws(Exception::class)
//    fun whenCreateProject_thenServiceRetrieveSavedProject() = runTest {
//        val mockedNotification = Notification(
//            "mockedProjectName",
//            "creator_id",
//            setOf(SimpleContributor("creator_id", emptySet())),
//            ZoneId.systemDefault(),
//        )
//        val savedNotification = Notification(
//            "savedProjectName",
//            "creator_id",
//            setOf(SimpleContributor("creator_id", emptySet())),
//            ZoneId.systemDefault(),
//        )
//        coEvery { repository.save(mockedNotification) } returns savedNotification
//        val outputProject = service.createProject(mockedNotification)
//        assertThat(outputProject).isSameAs(savedNotification)
//        coVerify { repository.save(mockedNotification) }
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun whenUpdateProject_thenServiceRetrieveSavedProject() = runTest {
//        val mockedSimpleContributor = SimpleContributor("mockedId")
//        val mockedExistingNotification = mockk<Notification>()
//        every {
//            mockedExistingNotification.setProperty(Notification::name.name) value "mockedUpdatedProjectName"
//        } just Runs
//        every {
//            mockedExistingNotification.setProperty(Notification::attributes.name) value emptySet<Attribute<Any>>()
//        } just Runs
//        every {
//            mockedExistingNotification.setProperty(Notification::requirements.name) value emptySet<Attribute<Any>>()
//        } just Runs
//        val mockedUpdateNotification = Notification(
//            "mockedUpdatedProjectName",
//            "creator_id",
//            setOf(SimpleContributor("creator_id", emptySet())),
//            ZoneId.systemDefault(),
//        )
//        val savedNotification = Notification(
//            "savedProjectName",
//            "creator_id",
//            setOf(SimpleContributor("creator_id", emptySet())),
//            ZoneId.systemDefault(),
//        )
//        coEvery {
//            repository.findByIdForContributor(
//                ListNotificationsFilter(listOf("id1"), listOf("mockedId")),
//                mockedSimpleContributor,
//            )
//        } returns mockedExistingNotification
//        coEvery { repository.save(any()) } returns savedNotification
//        val outputProject =
//            service.updateProject("id1", mockedUpdateNotification, mockedSimpleContributor)
//        assertThat(outputProject).isSameAs(savedNotification)
//        coVerifyAll {
//            repository.findByIdForContributor(
//                ListNotificationsFilter(listOf("id1"), listOf("mockedId")),
//                mockedSimpleContributor,
//            )
//            repository.findByIdForContributor(
//                ListNotificationsFilter(listOf("id1"), null),
//                null,
//            )
//            repository.save(any())
//        }
//        verifyAll {
//            mockedExistingNotification.setProperty(Notification::name.name) value "mockedUpdatedProjectName"
//            mockedExistingNotification.setProperty(Notification::attributes.name) value emptySet<Attribute<Any>>()
//            mockedExistingNotification.setProperty(Notification::requirements.name) value emptySet<Attribute<Any>>()
//        }
//        confirmVerified(mockedExistingNotification, repository)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun whenUpdateProject_thenServiceRetrieveUpdatedProject() = runTest {
//        val mockedNotification = Notification(
//            "mockedProjectName",
//            "creator_id",
//            setOf(SimpleContributor("creator_id", emptySet())),
//            ZoneId.systemDefault(),
//        )
//        val updatedNotification = Notification(
//            "updatedProjectName",
//            "creator_id",
//            setOf(SimpleContributor("creator_id", emptySet())),
//            ZoneId.systemDefault(),
//        )
//        coEvery { repository.save(mockedNotification) } returns updatedNotification
//        val outputProject = service.createProject(mockedNotification)
//        assertThat(outputProject).isSameAs(updatedNotification)
//        coVerify { repository.save(mockedNotification) }
//    }
//}
