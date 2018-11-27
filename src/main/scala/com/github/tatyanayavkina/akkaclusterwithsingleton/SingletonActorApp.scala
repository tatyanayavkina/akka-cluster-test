package com.github.tatyanayavkina.akkaclusterwithsingleton

import java.time.LocalDateTime
import java.util.UUID

import akka.actor.{ActorSystem, PoisonPill}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings}
import com.github.tatyanayavkina.akkaclusterwithsingleton.TheOne.SendMessageToRabbit
import com.github.tatyanayavkina.akkaclusterwithsingleton.settings.ProjectSettings
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration._

object SingletonActorApp extends App {

  val projectSettings = pureconfig.loadConfigOrThrow[ProjectSettings]
  val config = ConfigFactory.load()

  implicit lazy val system = ActorSystem(config.getString("clustering.cluster.name"), config)
  implicit lazy val executionContext = system.dispatcher

  sys.addShutdownHook(() => {
    if (system != null) {
      system.terminate()
      Await.ready(system.whenTerminated, 30.seconds)
    }
  })

  val instanceId = s"${config.getString("akka.remote.netty.tcp.hostname")}-${config.getString("akka.remote.netty.tcp.port")}"
  system.actorOf(ClusterSingletonManager.props(
    singletonProps = TheOne.props(s"single-instance-$instanceId", projectSettings.rabbitSettings),
    terminationMessage = PoisonPill,
    settings = ClusterSingletonManagerSettings(system)
  ), name = "the-one")

  val proxy = system.actorOf(ClusterSingletonProxy.props(
    singletonManagerPath = "/user/the-one",
    settings = ClusterSingletonProxySettings(system)),
    name = "the-one-proxy")
  // todo: only one message should be processed by TheOne actor
  system.scheduler.schedule((60 - LocalDateTime.now().getSecond).seconds, 1.minute, proxy, SendMessageToRabbit(instanceId))
}
