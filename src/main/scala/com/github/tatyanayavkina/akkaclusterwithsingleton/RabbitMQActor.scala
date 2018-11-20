package com.github.tatyanayavkina.akkaclusterwithsingleton

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.github.tatyanayavkina.akkaclusterwithsingleton.RabbitMQActor.{End, Initialize, SendMessage}
import com.newmotion.akka.rabbitmq._

class RabbitMQActor extends Actor with ActorLogging {
  import context.system
  var initialized = false
  var connection: ActorRef = _

  val exchange = "amq.fanout"

  override def receive: Receive = {
    case Initialize => doInitialize()
    case SendMessage(message) => sendMessage(message)
    case End => stop()
  }

  private def doInitialize(): Unit = {
    if (initialized) {
      return
    }

    val factory = new ConnectionFactory()
    connection = context.actorOf(ConnectionActor.props(factory), "rabbitmq")

    def setupPublisher(channel: Channel, self: ActorRef) {
      val queue = channel.queueDeclare().getQueue
      channel.queueBind(queue, exchange, "")
    }
    connection ! CreateChannel(ChannelActor.props(setupPublisher), Some("publisher"))

    initialized = true
  }

  private def stop(): Unit = {
    context.stop(connection)
    context.stop(self)
  }

  private def sendMessage(message: String): Unit = {
    if (!initialized) {
      return
    }

    val publisher = system.actorSelection("/user/rabbit-mq-sender/rabbitmq/publisher")
    def publish(channel: Channel) {
      channel.basicPublish(exchange, "", null, toBytes(message))
    }
    publisher ! ChannelMessage(publish, dropIfNoChannel = false)
  }

  private def toBytes(x: String) = x.toString.getBytes("UTF-8")
}

object RabbitMQActor {
  case object Initialize
  case class SendMessage(message: String)
  case object End

  def props: Props = Props(new RabbitMQActor)
}
