package com.github.tatyanayavkina.akkaclusterwithsingleton


import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import com.github.tatyanayavkina.akkaclusterwithsingleton.TheOne._

class TheOne(instance: String, rabbitMQ: RabbitMQ) extends Actor with ActorLogging {
  log.info(s"$instance started")

  override def receive: Receive = {
    case SendMessageToRabbit(from) => sendMessage(from)
    case PoisonPill =>
      log.info(s"$instance got poison pill")
      rabbitMQ.closeChannel()
      context.stop(self)
  }

  private def sendMessage(initiator: String): Unit = {
    val message = MessageCreator.createMessage(initiator, instance)
    log.info(message)
    rabbitMQ.sendMessage(message)
  }
}

object TheOne {
  case class SendMessageToRabbit(initiator: String)
  case object EndProcess

  def props(instance: String, rabbitMQ: RabbitMQ): Props = Props(new TheOne(instance, rabbitMQ))
}
