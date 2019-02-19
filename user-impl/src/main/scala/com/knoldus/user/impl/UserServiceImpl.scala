package com.knoldus.user.impl

import akka.{Done, NotUsed}
import com.knoldus.user.impl.eventSourcing._
import com.knoldus.user.impl.models._
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRef, PersistentEntityRegistry}

import scala.concurrent.{ExecutionContext, Future}


class UserServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)
                     (implicit ec: ExecutionContext) extends UserService {

  override def addUser(): ServiceCall[UserDetails, UserResponse] = {
    request =>
      getUserDetails(request.orgId.toString).flatMap {
        case Some(_) => Future.successful(UserResponse(s"User with ID: ${request.orgId} already exists!!"))
        case None => ref(request.orgId.toString).ask(AddUserCommand(request)).map {
          case Done => UserResponse(s"User added successfully with ID: ${request.orgId}")
        }
      }
  }

  override def updateUser(): ServiceCall[UpdateRequest, UserResponse] = {
    request =>
      getUserDetails(request.orgId.toString).flatMap {
        case Some(user) => ref(user.orgId.toString).ask(UpdateUserCommand(request.orgId, request.name)).map {
          case Done => UserResponse(s"Updated user name successfully to: ${request.name}")
        }
        case None => Future.successful(UserResponse(s"User doesn't exists with ID: ${request.orgId}"))
      }
  }

  override def getUser(orgId: String): ServiceCall[NotUsed, GetUserResponse] = {
    _ =>
      getUserDetails(orgId).map {
        case Some(user) => GetUserResponse(Some(user), None)
        case None => GetUserResponse(None, Some(s"User doesn't exists with ID: $orgId"))
      }
  }

  override def deleteUser(orgId: Int): ServiceCall[NotUsed, UserResponse] = {
    _ =>
      getUserDetails(orgId.toString).flatMap {
        case Some(user) => ref(user.orgId.toString).ask(DeleteUserCommand(user.orgId)).map {
          case Done => UserResponse(s"User Deleted successfully with ID: $orgId")
        }
        case None => Future.successful(UserResponse(s"User doesn't exists with ID: $orgId"))
      }
  }

  private def ref(id: String): PersistentEntityRef[UserCommand[_]] = {
    persistentEntityRegistry.refFor[UserEntity](id)
  }

  private def getUserDetails(orgId: String): Future[Option[UserDetails]] = {
    ref(orgId).ask(GetUserCommand(orgId))
  }
}
