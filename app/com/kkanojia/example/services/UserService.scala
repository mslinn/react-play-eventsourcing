package com.kkanojia.example.services

import javax.inject.Inject
import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import com.kkanojia.example.actors.UserActor.{CreateUser, UserCreationFailed, UserCreationSuccess, UserRetrievalSuccess}
import com.kkanojia.example.actors.UserManager.RetrieveUser
import com.kkanojia.example.models.User
import com.kkanojia.example.modules.ActorSystemInitializer
import play.api.Logger
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class UserService @Inject()(implicit system: ActorSystem, ec: ExecutionContext) {
  implicit val timeout: Timeout = 15 seconds

  lazy val userManager: ActorRef = ActorSystemInitializer.userManagerActorRef

  def createUser(email: String): Future[Option[User]] = {
    userManager ? CreateUser(User(email)) map {
      case UserCreationSuccess(createdUser) =>
        Some(createdUser)

      case UserCreationFailed(cause) =>
        Logger.error(s"Error occurred while creating user ${ cause.getMessage }")
        None
    }
  }

  def retrieveUser(email: String): Future[Option[User]] = {
    userManager ? RetrieveUser(email) map {
      case UserRetrievalSuccess(user) => Some(user)
      case _ => None
    }
  }
}
