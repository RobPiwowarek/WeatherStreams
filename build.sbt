name := "WeatherStreams"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies := Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.11",
  "com.typesafe.akka" %% "akka-stream" % "2.5.11",
  "com.typesafe.akka" %% "akka-http" % "10.1.0",

  "org.scalatest" %% "scalatest" % "3.2.0-SNAP10" % Test,
  "com.github.tomakehurst" % "wiremock-standalone" % "2.15.0" % Test
)