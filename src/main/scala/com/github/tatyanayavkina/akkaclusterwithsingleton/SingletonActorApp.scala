package com.github.tatyanayavkina.akkaclusterwithsingleton

import java.time.LocalDateTime

import akka.actor.ActorSystem
import com.github.tatyanayavkina.akkaclusterwithsingleton.TheOne.SendMessageToRabbit

import scala.concurrent.duration._

object SingletonActorApp extends App {

  implicit val system = ActorSystem("akka-singleton")
  implicit val executionContext = system.dispatcher

  val theOneActor = system.actorOf(TheOne.props("single-instance"), "the-one")
  system.scheduler.schedule((60 - LocalDateTime.now().getSecond).seconds, 1.minute, theOneActor, SendMessageToRabbit)
}
