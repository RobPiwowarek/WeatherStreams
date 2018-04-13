name := "WeatherStreams"

version := "0.2-SNAPSHOT"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.11",
  "com.typesafe.akka" %% "akka-stream" % "2.5.11",
  "com.typesafe.akka" %% "akka-http" % "10.1.0",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.1",


  "com.lightbend.akka" %% "akka-stream-alpakka-slick" % "0.17",



  "org.scalatest" %% "scalatest" % "3.2.0-SNAP10" % Test,
  "com.github.tomakehurst" % "wiremock-standalone" % "2.15.0" % Test,
  "org.scalacheck" %% "scalacheck" % "1.13.5" % Test // for some reason scalatest requires it even if not used
)