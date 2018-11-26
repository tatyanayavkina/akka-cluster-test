package com.github.tatyanayavkina.akkaclusterwithsingleton.settings

case class RabbitSettings(queueName: String, exchangeType: String, host: String)

case class ProjectSettings(rabbitSettings: RabbitSettings)
