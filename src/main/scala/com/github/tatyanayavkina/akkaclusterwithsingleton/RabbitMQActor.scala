package com.github.tatyanayavkina.akkaclusterwithsingleton

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.github.tatyanayavkina.akkaclusterwithsingleton.RabbitMQActor.{End, SendMessage}
import com.github.tatyanayavkina.akkaclusterwithsingleton.settings.RabbitSettings
import com.newmotion.akka.rabbitmq._

class RabbitMQActor(rabbitSettings: RabbitSettings) extends Actor with ActorLogging {

  private val factory = new ConnectionFactory()
  private val rabbitMqConnection = context.actorOf(ConnectionActor.props(factory), "rabbitmq")
  private val publisher = createPublisherAndBind(rabbitMqConnection, rabbitSettings)

  override def receive: Receive = {
    case SendMessage(message) => sendMessage(message)
    case End =>
      context.stop(rabbitMqConnection)
      context.stop(self)
  }

  private def sendMessage(message: String): Unit = {
    def publish(channel: Channel) {
      channel.basicPublish(rabbitSettings.exchangeType, "", null, toBytes(message))
    }
    publisher ! ChannelMessage(publish, dropIfNoChannel = false)
  }

  private def toBytes(x: String) = x.toString.getBytes("UTF-8")

  private def createPublisherAndBind(rabbitMqConnection: ActorRef, rabbitSettings: RabbitSettings): ActorRef = {
    def setupPublisher(channel: Channel, self: ActorRef) {
      val queue = channel.queueDeclare(rabbitSettings.queueName, true, false, false, null).getQueue
      channel.queueBind(queue, rabbitSettings.exchangeType, "")
    }

    rabbitMqConnection.createChannel(ChannelActor.props(setupPublisher), Some("publisher"))
  }
}

object RabbitMQActor {
  case object Initialize
  case class SendMessage(message: String)
  case object End

  def props(rabbitSettings: RabbitSettings): Props = Props(new RabbitMQActor(rabbitSettings))
}
