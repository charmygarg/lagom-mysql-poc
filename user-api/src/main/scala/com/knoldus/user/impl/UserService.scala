package com.knoldus.user.impl

import akka.NotUsed
import com.knoldus.user.impl.models._
import com.lightbend.lagom.scaladsl.api.Service.{named, restCall}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.transport.Method._
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceAcl, ServiceCall}

import scala.concurrent.Future

trait UserService extends Service {

  override final def descriptor: Descriptor = {

    named("user-service")
      .withCalls(
        restCall(POST, "/user/add", addUser _),
        restCall(GET, "/user/get?id", getUser _),
        restCall(PUT, "/user/update", updateUser _),
        restCall(DELETE, "/user/delete?id", deleteUser _)
      ).withAutoAcl(true).withAcls(
      ServiceAcl.forMethodAndPathRegex(Method.OPTIONS, "/user.*")
    )
  }

  def addUser(): ServiceCall[UserDetails, UserResponse]

  def updateUser(): ServiceCall[UpdateRequest, UserResponse]

  def getUser(id: String): ServiceCall[NotUsed, GetUserResponse]

  def deleteUser(id: Int): ServiceCall[NotUsed, UserResponse]

}
