import Dependencies._

lazy val commonSettings = Seq(
  name := "akka-cluster-with-singleton-test",
  organization := "com.github.tatyanayavkina",
  version := "0.1.0",
  scalaVersion := "2.12.6",
  fork in run := true
)

lazy val root  = (project in file("."))
  .settings(
    commonSettings
  )
  .settings(
    libraryDependencies ++= Seq(
      logbackClassic,
      scalaLogging,
      akkaActor,
      akkaRabbitMq,
      akkaCluster,
      akkaClusterMetrics,
      pureConfig
    )
  )