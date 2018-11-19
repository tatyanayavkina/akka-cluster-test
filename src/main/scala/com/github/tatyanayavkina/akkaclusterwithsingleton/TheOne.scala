package com.github.tatyanayavkina.akkaclusterwithsingleton

import akka.actor.{Actor, ActorLogging, Props}
import com.github.tatyanayavkina.akkaclusterwithsingleton.TheOne.{EndProcess, SendMessageToRabbit}

class TheOne(instance: String) extends Actor with ActorLogging {

  override def receive: Receive = {
    case SendMessageToRabbit => log.info("Send message to rabbit from $instance")
    case EndProcess =>
      log.info("Get poison pill")
      context.stop(self)
  }
}

object TheOne {
  case object SendMessageToRabbit
  case object EndProcess

  def props(instance: String): Props = Props(new TheOne(instance))
}
