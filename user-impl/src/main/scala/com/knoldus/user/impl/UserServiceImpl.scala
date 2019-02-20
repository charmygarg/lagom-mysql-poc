package com.knoldus.user.impl

import akka.{Done, NotUsed}
import com.knoldus.user.impl.constants.UserConstants._
import com.knoldus.user.impl.eventSourcing._
import com.knoldus.user.impl.models._
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRef, PersistentEntityRegistry}
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)
                     (implicit ec: ExecutionContext) extends UserService {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def addUser(): ServiceCall[UserDetails, UserResponse] = {
    request =>
      logger.info(s"Request received to add user details for ID: ${request.orgId}")
      getUserDetails(request.orgId).flatMap {
        case Some(_) => logger.error(s"Failed to add details as user already exists: ${request.orgId}")
          Future.successful(UserResponse(UserAlreadyExists))
        case None => ref(request.orgId.toString).ask(AddUserCommand(request)).map {
          case Done => UserResponse(AddedSuccessfully)
        }
      }
  }

  override def updateUser(): ServiceCall[UpdateRequest, UserResponse] = {
    request =>
      logger.info(s"Request received to update user name for ID: ${request.orgId}")
      getUserDetails(request.orgId).flatMap {
        case Some(user) => ref(user.orgId.toString).ask(UpdateUserCommand(request.orgId, request.name)).map {
          case Done => UserResponse(UpdatedSuccessfully)
        }
        case None => logger.error(s"Failed to update user name for invalid ID: ${request.orgId}")
          Future.successful(UserResponse(InvalidUser))
      }
  }

  private def getUserDetails(orgId: Int): Future[Option[UserDetails]] = ref(orgId.toString).ask(GetUserCommand(orgId))

  private def ref(id: String): PersistentEntityRef[UserCommand[_]] = persistentEntityRegistry.refFor[UserEntity](id)

  override def getUser(orgId: Int): ServiceCall[NotUsed, GetUserResponse] = {
    _ =>
      logger.info(s"Request received to get user details for ID: $orgId")
      getUserDetails(orgId).map {
        case Some(user) => GetUserResponse(Some(user), None)
        case None => logger.error(s"Failed to get user details for invalid ID: $orgId")
          GetUserResponse(None, Some(InvalidUser))
      }
  }

  override def deleteUser(orgId: Int): ServiceCall[NotUsed, UserResponse] = {
    _ =>
      logger.info(s"Request received to delete user for ID: $orgId")
      getUserDetails(orgId).flatMap {
        case Some(user) => ref(user.orgId.toString).ask(DeleteUserCommand(user.orgId)).map {
          case Done => UserResponse(DeletedSuccessfully)
        }
        case None => logger.error(s"Failed to delete user for invalid ID: $orgId")
          Future.successful(UserResponse(InvalidUser))
      }
  }

}
