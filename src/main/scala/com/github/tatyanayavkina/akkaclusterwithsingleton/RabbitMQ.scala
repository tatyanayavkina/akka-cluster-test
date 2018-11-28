package com.github.tatyanayavkina.akkaclusterwithsingleton

import akka.actor.ActorRef
import com.github.tatyanayavkina.akkaclusterwithsingleton.settings.RabbitSettings
import com.newmotion.akka.rabbitmq._

class RabbitMQ(rabbitSettings: RabbitSettings, publisher: ActorRef, destroyFunction: () => Unit) {

  def sendMessage(message: String): Unit = {
    def publish(channel: Channel) {
      channel.basicPublish(rabbitSettings.exchangeType, "", null, toBytes(message))
    }

    publisher ! ChannelMessage(publish, dropIfNoChannel = false)
  }

  def closeChannel(): Unit = {
    destroyFunction()
  }

  private def toBytes(x: String) = x.toString.getBytes("UTF-8")
}