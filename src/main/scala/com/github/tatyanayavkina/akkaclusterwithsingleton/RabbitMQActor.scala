package com.github.tatyanayavkina.akkaclusterwithsingleton

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.github.tatyanayavkina.akkaclusterwithsingleton.RabbitMQActor.{End, SendMessage}
import com.newmotion.akka.rabbitmq._

class RabbitMQActor(publisher: ActorRef, exchange: String) extends Actor with ActorLogging {
 //todo: how to close connection
  override def receive: Receive = {
    case SendMessage(message) => sendMessage(message)
    case End =>
      context.stop(publisher)
      context.stop(self)
  }

  private def sendMessage(message: String): Unit = {
    def publish(channel: Channel) {
      channel.basicPublish(exchange, "", null, toBytes(message))
    }
    publisher ! ChannelMessage(publish, dropIfNoChannel = false)
  }

  private def toBytes(x: String) = x.toString.getBytes("UTF-8")
}

object RabbitMQActor {
  case class SendMessage(message: String)
  case object End

  def props(publisher: ActorRef, exchange: String): Props = Props(new RabbitMQActor(publisher, exchange))
}
