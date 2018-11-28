package com.github.tatyanayavkina.akkaclusterwithsingleton

import akka.actor.{ActorRef, ActorSystem}
import com.github.tatyanayavkina.akkaclusterwithsingleton.settings.RabbitSettings
import com.newmotion.akka.rabbitmq._

object RabbitMQFactory {

  def getRabbitMq(rabbitSettings: RabbitSettings)(implicit system: ActorSystem): RabbitMQ = {
    val factory = new ConnectionFactory()
    factory.setHost(rabbitSettings.host)
    val connection = system.actorOf(ConnectionActor.props(factory), "rabbitmq")
    val publisher = createPublisherAndBind(connection, rabbitSettings)

    def closeConnection(): Unit = {
      system.stop(publisher)
      system.stop(connection)
    }

    new RabbitMQ(rabbitSettings, publisher, closeConnection)
  }

  private def createPublisherAndBind(connection: ActorRef, rabbitSettings: RabbitSettings): ActorRef = {
    def setupPublisher(channel: Channel, self: ActorRef) {
      val queue = channel.queueDeclare(rabbitSettings.queueName, true, false, false, null).getQueue
      channel.queueBind(queue, rabbitSettings.exchangeType, "")
    }

    connection.createChannel(ChannelActor.props(setupPublisher), Some("publisher"))
  }

}
