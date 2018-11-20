# akka-cluster-test
Test project with akka-cluster

# Here I will place some commands and information useful for my developing

docker run -d --hostname my-rabbit --name some-rabbit -p 8080:15672 -p 5672:5672 rabbitmq:3-management
[INFO] [11/20/2018 15:55:00.917] [akka-singleton-akka.actor.default-dispatcher-3] 
[akka://akka-singleton/user/rabbitmq/publisher] Message [com.newmotion.akka.rabbitmq.ChannelMessage] 
from Actor[akka://akka-singleton/user/rabbit-mq-sender#416077952] to Actor[akka://akka-singleton/user/rabbitmq/publisher] 
was not delivered. [1] dead letters encountered. 
This logging can be turned off or adjusted with configuration settings 'akka.log-dead-letters' and 
'akka.log-dead-letters-during-shutdown'.

