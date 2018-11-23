import sbt._

object Dependencies {
  // Versions
  lazy val logbackVersion = "1.2.3"
  lazy val scalaLoggingVersion = "3.9.0"
  lazy val akkaVersion = "2.5.18"
  lazy val akkaRabbitMqVersion = "5.0.0"
  lazy val commonsCollectionsVersion = "4.1"
  lazy val pureConfigVersion = "0.9.1"
  lazy val scalaTestVersion = "3.0.5"

  // Libraries
  val logbackClassic = "ch.qos.logback" % "logback-classic" % logbackVersion
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val pureConfig = "com.github.pureconfig" %% "pureconfig" % pureConfigVersion
  val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion % Test
  val akkaTest = "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
  val akkaRabbitMq = "com.newmotion" %% "akka-rabbitmq" % akkaRabbitMqVersion
  val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % akkaVersion
  val akkaClusterMetrics = "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion
}
