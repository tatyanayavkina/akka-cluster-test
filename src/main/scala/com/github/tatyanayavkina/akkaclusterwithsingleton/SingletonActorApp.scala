package com.github.tatyanayavkina.akkaclusterwithsingleton

import java.time.LocalDateTime

import akka.actor.{ActorSystem, PoisonPill}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings}
import com.github.tatyanayavkina.akkaclusterwithsingleton.TheOne.SendMessageToRabbit
import com.github.tatyanayavkina.akkaclusterwithsingleton.settings.ProjectSettings
import com.typesafe.config.{ConfigFactory, ConfigValueFactory}

import scala.concurrent.Await
import scala.concurrent.duration._

object SingletonActorApp extends App {

  startup(Seq("2551", "2552", "0"))

  def startup(ports: Seq[String]): Unit = {
    val projectSettings = pureconfig.loadConfigOrThrow[ProjectSettings]

    ports foreach { port =>
      implicit var system: ActorSystem = null

      sys.addShutdownHook {
        if (system != null) {
          system.terminate()
          Await.ready(system.whenTerminated, 30.seconds)
        }
      }

      val config = ConfigFactory.load()
        .withValue("akka.remote.artery.canonical.port", ConfigValueFactory.fromAnyRef(port))

      system = ActorSystem("akka-singleton-cluster", config)
      implicit val executionContext = system.dispatcher

      system.actorOf(ClusterSingletonManager.props(
        singletonProps = TheOne.props(s"single-instance-$port", projectSettings.rabbitSettings),
        terminationMessage = PoisonPill,
        settings = ClusterSingletonManagerSettings(system)
      ), name = "the-one")

      val proxy = system.actorOf(ClusterSingletonProxy.props(
        singletonManagerPath = "/user/the-one",
        settings = ClusterSingletonProxySettings(system)),
        name = "the-one-proxy")

      system.scheduler.schedule((60 - LocalDateTime.now().getSecond).seconds, 1.minute, proxy, SendMessageToRabbit(port))
    }
  }
}
