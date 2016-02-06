name := "foosball-server"

version := "1.0"

scalaVersion := "2.11.5"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf-8")

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"


libraryDependencies ++= {
  val akkaVersion = "2.3.6"
  val sprayVersion = "1.3.2"
  Seq(
    "com.typesafe.akka" %% "akka-http-experimental" % "2.0.2",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "org.specs2" %% "specs2-core" % "2.3.11" % "test",
    "com.typesafe.play" %% "play-json" % "2.4.+"
  )
}
