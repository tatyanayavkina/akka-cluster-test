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
    commonSettings,
    mainClass in Compile := Some("com.github.tatyanayavkina.akkaclusterwithsingleton.SingletonActorApp")
  )
  .settings(
    libraryDependencies ++= Seq(
      logbackClassic,
      scalaLogging,
      akkaActor,
      akkaRabbitMq,
      pureConfig
    )
  )