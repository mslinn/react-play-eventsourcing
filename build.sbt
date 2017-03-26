name := """react-play-eventsourcing"""

version := "0.1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)

scalaVersion := "2.11.8"

resolvers += "Eventuate Releases" at "https://dl.bintray.com/rbmhtechnology/maven"

javaOptions in Test += "-Dconfig.resource=test.conf"
//Heroku
herokuAppName in Compile := "play-eventsourcing"
herokuJdkVersion in Compile := "1.8"


val akkaVersion = "2.4.17"
val evVer = "0.8.1"

libraryDependencies ++= Seq(
  ws,

  //Eventuate
  "com.rbmhtechnology" %% "eventuate-core" % evVer,
  "com.rbmhtechnology" %% "eventuate-log-leveldb" % evVer,

  //JSON
  "org.json4s" %% "json4s-jackson" % "3.3.0",
  "org.json4s" %% "json4s-ext" % "3.3.0",

  //UI
  "org.webjars" %% "webjars-play" % "2.5.0-2",
  "com.adrianhurt" %% "play-bootstrap" % "1.0-P25-B3",

  //TEST
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,

  "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
)

routesGenerator := InjectedRoutesGenerator
