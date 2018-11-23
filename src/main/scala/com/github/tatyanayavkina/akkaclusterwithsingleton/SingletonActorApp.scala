package com.github.tatyanayavkina.akkaclusterwithsingleton

import java.time.LocalDateTime

import akka.actor.ActorSystem
import com.github.tatyanayavkina.akkaclusterwithsingleton.TheOne.SendMessageToRabbit
import com.github.tatyanayavkina.akkaclusterwithsingleton.settings.{ProjectSettings, RabbitSettings}
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration._

object SingletonActorApp extends App {

  startup(Seq("2551", "2552", "0"))

  def startup(ports: Seq[String]): Unit = {
    val projectSettings = pureconfig.loadConfigOrThrow[ProjectSettings]

    ports foreach { port =>

      val config = ConfigFactory.parseString(
        s"""
        akka.remote.artery.canonical.port=$port
        """).withFallback(ConfigFactory.load())

      implicit val system = ActorSystem("akka-singleton-cluster", config)
      implicit val executionContext = system.dispatcher

      sys.addShutdownHook {
        system.terminate()
        Await.ready(system.whenTerminated, 30.seconds)
      }

      val theOneActor = system.actorOf(TheOne.props(s"single-instance-$port", projectSettings.rabbitSettings), "the-one")

      system.scheduler.schedule((60 - LocalDateTime.now().getSecond).seconds, 1.minute, theOneActor, SendMessageToRabbit)
    }
  }
}
