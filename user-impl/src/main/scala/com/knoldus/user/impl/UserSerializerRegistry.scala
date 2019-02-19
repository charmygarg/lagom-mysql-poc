package com.knoldus.user.impl

import com.knoldus.user.impl.eventSourcing.{AddUserCommand, GetUserCommand, UserAdded, UserState}
import com.knoldus.user.impl.models.{UserDetails, UserResponse}
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import scala.collection.immutable.Seq

object UserSerializerRegistry extends JsonSerializerRegistry {

  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[UserResponse],
    JsonSerializer[UserDetails],
    JsonSerializer[AddUserCommand],
    JsonSerializer[GetUserCommand],
    JsonSerializer[UserAdded],
    JsonSerializer[UserState],
  )

}
