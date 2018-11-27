## akka-cluster-test
Test project with akka-cluster

## Here I will place some commands and information useful for my developing

Run RabbitMQ in docker with management dashboard
```
docker run -d --hostname my-rabbit --name some-rabbit -p 8080:15672 -p 5672:5672 rabbitmq:3-management
```

Create docker image for current application
```
sbt docker:publishLocal
```

Run several containers with docker-compose
```
docker-compose up
```

Scale services by docker command
```
docker-compose up --scale theone=6
```