package com.kkanojia.example.modules

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.Timeout
import com.google.inject.{Inject, Singleton}
import com.kkanojia.example.actors.{TradeAggregateViewActor, UserManager}
import com.rbmhtechnology.eventuate.ReplicationEndpoint
import com.rbmhtechnology.eventuate.log.leveldb.LeveldbEventLog
import play.api.Logger
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

object ActorSystemInitializer {
  implicit val timeout: Timeout = 5 seconds

  def userManagerActorRef(implicit system: ActorSystem): ActorRef =
    Await.result(system.actorSelection(s"application/user/${ UserManager.NAME }").resolveOne(), timeout.duration)
}

@Singleton
class ActorSystemInitializer @Inject()(implicit system: ActorSystem) {
  Logger.info("Initializing ActorSystem")

  val endpoint: ReplicationEndpoint = ReplicationEndpoint(id => LeveldbEventLog.props(id))(system)
  endpoint.activate()
  Logger.info(s"Activated ${ endpoint.applicationName } endpoint with id ${ endpoint.id }")

  // Initialise event log
  val eventLog: ActorRef = endpoint.logs(ReplicationEndpoint.DefaultLogName)
  Logger.info(s"Initialized ${ eventLog.path } eventLog")

  // Init User Manager
  val userManagerProps = Props(
    new UserManager(UserManager.ID, Some(UserManager.ID), eventLog)
  )
  val userManagerActorRef: ActorRef = system.actorOf(userManagerProps, UserManager.NAME)
  Logger.info(s"userManagerActorRef has path ${ userManagerActorRef.path }")

  val cumulativeTradeViewProps = Props(
    new TradeAggregateViewActor(TradeAggregateViewActor.ID, eventLog)
  )
  val tradeAggregateViewActorActorRef = system.actorOf(cumulativeTradeViewProps, TradeAggregateViewActor.NAME)
  Logger.info(s"tradeAggregateViewActorActorRef has path ${ tradeAggregateViewActorActorRef.path }")

  Logger.info("ActorSystem Initialization complete.")
}
