name := "WeatherStreams"

version := "0.2-SNAPSHOT"

scalaVersion := "2.12.4"

retrieveManaged := true

mainClass in assembly := Some("server.WebServer")

test in assembly := {}

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard // needed to avoid deduplicate errors
  case PathList("reference.conf") => MergeStrategy.concat // needed for akka config files
  case x => MergeStrategy.first
}

libraryDependencies ++= Seq(
  "com.typesafe.akka"  %% "akka-actor"           % "2.5.11",
  "com.typesafe.akka"  %% "akka-stream"          % "2.5.11",
  "com.typesafe.akka"  %% "akka-http"            % "10.1.0",
  "com.typesafe.akka"  %% "akka-http-spray-json" % "10.1.1",
  "ch.megard"          %% "akka-http-cors"       % "0.3.0",
  "org.asynchttpclient" % "async-http-client"    % "2.4.5",
  "com.netaporter"     %% "scala-uri"            % "0.4.16",

  "com.typesafe.slick" %% "slick"               % "3.2.3",
  "org.slf4j"           % "slf4j-nop"           % "1.6.4", // needed for slick
  "com.typesafe.slick" %% "slick-hikaricp"      % "3.2.3", // not necessarily needed for slick
  "org.mariadb.jdbc"    % "mariadb-java-client" % "1.1.7",

  "com.lightbend.akka" %% "akka-stream-alpakka-slick" % "0.18",

  "com.typesafe" % "config" % "1.3.2",
  "com.sun.mail" % "javax.mail" % "1.6.1",

  "org.scalatest"          %% "scalatest"          % "3.2.0-SNAP10"    % Test,
  "com.github.tomakehurst" % "wiremock-standalone" % "2.15.0"          % Test,
  "org.scalacheck"         %% "scalacheck"         % "1.13.5"          % Test // for some reason scalatest requires it even if not used
)
