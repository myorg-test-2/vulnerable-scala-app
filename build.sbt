name := "vulnerable-scala-app"
version := "0.1.0"
scalaVersion := "2.12.8"

mainClass in (Compile, run) := Some("com.vulnerable.Main")

// 60+ vulnerable dependencies from 2019 for 200+ total vulnerabilities

libraryDependencies ++= Seq(
  // JSON (Play JSON kept as standalone artifact)
  "com.typesafe.play" %% "play-json" % "2.7.3",

  // Akka ecosystem (old vulnerable versions)
  "com.typesafe.akka" %% "akka-actor" % "2.5.23",
  "com.typesafe.akka" %% "akka-stream" % "2.5.23",
  "com.typesafe.akka" %% "akka-http" % "10.1.8",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.8",

  // Database
  "com.typesafe.slick" %% "slick" % "3.3.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.1",
  "mysql" % "mysql-connector-java" % "5.1.47",
  "org.postgresql" % "postgresql" % "42.2.5",
  "com.h2database" % "h2" % "1.4.199",

  // JSON processing
  "org.json4s" %% "json4s-jackson" % "3.6.6",
  "org.json4s" %% "json4s-native" % "3.6.6",
  "io.circe" %% "circe-core" % "0.11.1",
  "io.circe" %% "circe-generic" % "0.11.1",
  "io.circe" %% "circe-parser" % "0.11.1",

  // HTTP clients
  "org.scalaj" %% "scalaj-http" % "2.4.1",
  "com.softwaremill.sttp" %% "core" % "1.5.19",
  "com.squareup.okhttp3" % "okhttp" % "3.14.2",

  // AWS SDK (massive transitive dependencies)
  "com.amazonaws" % "aws-java-sdk" % "1.11.327",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.327",
  "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.11.327",

  // Logging
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",

  // XML/YAML processing
  "org.scala-lang.modules" %% "scala-xml" % "1.2.0",
  "net.jcazevedo" %% "moultingyaml" % "0.4.1",
  "org.yaml" % "snakeyaml" % "1.24",

  // Security/Crypto (old versions)
  "org.mindrot" % "jbcrypt" % "0.3m",
  "commons-codec" % "commons-codec" % "1.12",

  // Apache Commons (many vulnerabilities)
  "commons-io" % "commons-io" % "2.6",
  "commons-fileupload" % "commons-fileupload" % "1.3.3",
  "org.apache.commons" % "commons-lang3" % "3.9",
  "org.apache.commons" % "commons-text" % "1.6",
  "org.apache.commons" % "commons-collections4" % "4.3",

  // Jackson (vulnerable versions)
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.9",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.9",

  // Testing frameworks
  "org.scalatest" %% "scalatest" % "3.0.7" % Test,
  "org.scalamock" %% "scalamock" % "4.2.0" % Test,

  // Guava (old vulnerable version)
  "com.google.guava" % "guava" % "27.1-jre",

  // Netty (vulnerabilities)
  "io.netty" % "netty-all" % "4.1.36.Final",

  // JWT
  "com.pauldijou" %% "jwt-core" % "2.1.0",

  // Redis
  "net.debasishg" %% "redisclient" % "3.10",

  // MongoDB
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.6.0",

  // Elasticsearch
  "com.sksamuel.elastic4s" %% "elastic4s-core" % "6.7.3",

  // Cats/functional libs
  "org.typelevel" %% "cats-core" % "1.6.0",

  // Configuration
  "com.typesafe" % "config" % "1.3.4",

  // Joda Time (deprecated but still used)
  "joda-time" % "joda-time" % "2.10.2",

  // Spray (old HTTP toolkit)
  "io.spray" %% "spray-json" % "1.3.5",

  // More vulnerable transitive dependencies
  "org.apache.httpcomponents" % "httpclient" % "4.5.8",
  "org.apache.httpcomponents" % "httpcore" % "4.4.11"
)
