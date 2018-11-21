package com.github.tatyanayavkina.akkaclusterwithsingleton

import java.time.LocalDateTime

import akka.actor.{ActorRef, ActorSystem}
import com.github.tatyanayavkina.akkaclusterwithsingleton.TheOne.SendMessageToRabbit
import com.github.tatyanayavkina.akkaclusterwithsingleton.settings.{ProjectSettings, RabbitSettings}
import com.newmotion.akka.rabbitmq._

import scala.concurrent.Await
import scala.concurrent.duration._

object SingletonActorApp extends App {
  val projectSettings = pureconfig.loadConfigOrThrow[ProjectSettings]

  implicit val system = ActorSystem("akka-singleton")
  implicit val executionContext = system.dispatcher

  sys.addShutdownHook {
    system.terminate()
    Await.ready(system.whenTerminated, 30.seconds)
  }

  val rabbitSettings = projectSettings.rabbitSettings
  val factory = new ConnectionFactory()
  val rabbitMqConnection = system.actorOf(ConnectionActor.props(factory), "rabbitmq")
  val publisher = createPublisherAndBind(rabbitMqConnection, rabbitSettings)

  val rabbitMQActor = system.actorOf(RabbitMQActor.props(publisher, rabbitSettings.exchangeType), "rabbit-mq-sender")
  val theOneActor = system.actorOf(TheOne.props("single-instance", rabbitMQActor), "the-one")

  system.scheduler.schedule((60 - LocalDateTime.now().getSecond).seconds, 1.minute, theOneActor, SendMessageToRabbit)

  private def createPublisherAndBind(rabbitMqConnection: ActorRef, rabbitSettings: RabbitSettings): ActorRef = {
    def setupPublisher(channel: Channel, self: ActorRef) {
      val queue = channel.queueDeclare(rabbitSettings.queueName, true, false, false, null).getQueue
      channel.queueBind(queue, rabbitSettings.exchangeType, "")
    }

    rabbitMqConnection.createChannel(ChannelActor.props(setupPublisher), Some("publisher"))
  }
}
