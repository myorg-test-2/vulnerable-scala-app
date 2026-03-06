name := "vulnerable-scala-app"
version := "0.1"
scalaVersion := "2.12.8"
libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.7.3",
  "com.typesafe.akka" %% "akka-actor" % "2.5.23",
  "com.typesafe.slick" %% "slick" % "3.3.1",
  "org.json4s" %% "json4s-jackson" % "3.6.6",
  "com.amazonaws" % "aws-java-sdk" % "1.11.327"
)
