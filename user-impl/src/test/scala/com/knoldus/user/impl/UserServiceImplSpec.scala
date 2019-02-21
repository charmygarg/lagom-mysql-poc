package com.knoldus.user.impl

import java.util.UUID
import java.util.UUID.randomUUID

import akka.actor.ActorSystem
import akka.actor.setup.ActorSystemSetup
import com.knoldus.user.api.UserService
import com.knoldus.user.api.models.{GetUserResponse, UserDetails, UserResponse}
import com.knoldus.user.impl.eventSourcing.{AddUserCommand, UserEntity}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.testkit.{PersistentEntityTestDriver, ReadSideTestDriver, ServiceTest}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import com.knoldus.user.impl.UserTestHelper.ValidUserDetails
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.knoldus.user.impl.UserTestHelper.OrgID

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

class UserServiceImplSpec extends WordSpec with Matchers with BeforeAndAfterAll {

  val persistentEntityRegistry: PersistentEntityRegistry = MockitoSugar.mock[PersistentEntityRegistry]

  private val server = ServiceTest.startServer(ServiceTest.defaultSetup.withJdbc(true)) { ctx =>
    new UserApplication(ctx) {
      override def serviceLocator: ServiceLocator.NoServiceLocator.type = NoServiceLocator
    }
  }

  val userService: UserService = server.serviceClient.implement[UserService]

  import server.materializer

  override def afterAll: Unit = server.stop()

  "addUser" should {

    "return success response" in {
      val creatorId = UUID.randomUUID

      for {
        created <- createItem(creatorId, ValidUserDetails)
        retrieved <- retrieveItem(ValidUserDetails.orgId)
      } yield {
        created should === (retrieved)
      }
    }
  }

  private def createItem(creatorId: UUID, details: UserDetails): Future[GetUserResponse] = {
    userService.addUser().invoke(details).map(x => GetUserResponse(Some(details), Some(x.message)))
  }

  private def retrieveItem(orgID: Int): Future[GetUserResponse] = {
    userService.getUser(OrgID).invoke
  }

}
