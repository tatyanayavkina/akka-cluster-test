import Dependencies._
import com.typesafe.sbt.packager.docker.ExecCmd

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
      akkaClusterTools,
      pureConfig
    )
  )
 .settings(
   mappings in Universal += file("docker.conf") -> "conf/application.conf"
 )

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-encoding", "UTF-8",
  "-Xlint",
)

version in Docker := "latest"
dockerExposedPorts in Docker := Seq(2552)
dockerEntrypoint in Docker := Seq("sh", "-c", "bin/clustering $*")
dockerCommands += ExecCmd("ENTRYPOINT", "/opt/docker/bin/akka-cluster-with-singleton-test", "-Dconfig.file=/opt/docker/conf/application.conf")
dockerRepository := Some("tatyanayavkina")
dockerBaseImage := "java"
enablePlugins(JavaAppPackaging)