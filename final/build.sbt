ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.13"

lazy val root = (project in file("."))
  .settings(
    name := "try"
  )


libraryDependencies ++= Seq(
  // JTokkit for BPE tokenization
  "com.knuddels" % "jtokkit" % "1.1.0",

  // Apache Hadoop MapReduce for distributed computing
  "org.apache.hadoop" % "hadoop-client" % "3.3.4" excludeAll (
    ExclusionRule("com.google.protobuf", "protobuf-java"),
    ExclusionRule("org.apache.hadoop.shaded", "protobuf-java"),
    ExclusionRule("org.apache.hadoop.thirdparty", "protobuf-java")),

  // Apache Spark for distributed LLM training
  "org.apache.spark" %% "spark-core" % "3.4.0" excludeAll (
    ExclusionRule("com.google.protobuf", "protobuf-java"),
    ExclusionRule("org.apache.hadoop.shaded", "protobuf-java"),
    ExclusionRule("org.apache.hadoop.thirdparty", "protobuf-java")),
  "org.apache.spark" %% "spark-mllib" % "3.4.0",

  // DeepLearning4J for neural network training
  "org.apache.spark" %% "spark-core" % "3.3.1",
  "org.deeplearning4j" % "dl4j-spark_2.12" % "1.0.0-beta7",
  "org.deeplearning4j" % "deeplearning4j-core" % "1.0.0-beta7",
  "org.deeplearning4j" % "deeplearning4j-nlp" % "1.0.0-beta7",
  "org.nd4j" % "nd4j-api" % "1.0.0-beta7",       // Core ND4j library
  "org.nd4j" % "nd4j-native" % "1.0.0-beta7",

  // Ollama
  "io.github.ollama4j" % "ollama4j" % "1.0.79",

  // Akka HTTP for RESTful API
  "com.typesafe.akka" %% "akka-http" % "10.2.10",
  "com.typesafe.akka" %% "akka-stream" % "2.6.21",
  "com.typesafe.akka" %% "akka-actor" % "2.6.21",
  "io.spray" %% "spray-json" % "1.3.6",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.10",


  // JSON Support
  "de.heikoseeberger" %% "akka-http-circe" % "1.38.2",  // For JSON support with Akka HTTP
  "io.circe" %% "circe-core" % "0.14.1",  // JSON library
  "io.circe" %% "circe-generic" % "0.14.1",  // For JSON serialization
  "io.circe" %% "circe-parser" % "0.14.1",

  // gRPC
  "io.grpc" % "grpc-netty-shaded" % "1.42.0", // gRPC netty (transport)
  "io.grpc" % "grpc-protobuf" % "1.42.0", // gRPC protobuf
  "io.grpc" % "grpc-stub" % "1.42.0", // gRPC stub

  // ScalaPB
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
  "com.thesamet.scalapb" %% "scalapb-runtime" % "0.11.0",

  // AWS
  //"com.amazonaws" % "aws-java-sdk-lambda" % "1.12.318", // AWS SDK for Lambda (if needed)
  //"com.amazonaws" % "aws-java-sdk-s3" % "1.12.318",    // AWS SDK for S3
  "com.amazonaws" % "aws-lambda-java-core" % "1.2.3",  // AWS Lambda core
  "com.amazonaws" % "aws-lambda-java-events" % "3.11.0", // Lambda event types
  "org.apache.httpcomponents" % "httpclient" % "4.5.13", // HTTP client (optional)
  "org.scala-lang" % "scala-library" % "2.12.13", // Scala version
  // AWS SDK 2.x dependencies
  "software.amazon.awssdk" % "s3" % "2.20.22",    // AWS SDK for S3 (2.x version)
  "software.amazon.awssdk" % "lambda" % "2.20.22", // AWS SDK for Lambda (2.x version)
  "org.scala-lang" % "scala-library" % "2.13.8",

  // protobuf
  "com.google.protobuf" % "protobuf-java" % "3.19.4",

  // OpenBLAS
  "org.bytedeco" % "openblas-platform" % "0.3.28-1.5.11",

  // SLF4J and Logback for logging
  "ch.qos.logback" % "logback-classic" % "1.2.11",
  "org.slf4j" % "slf4j-api" % "1.7.36",

  // TypeSafe Config for configuration management
  "com.typesafe" % "config" % "1.4.1",
  "com.typesafe" %% "ssl-config-core" % "0.6.1",

  // ScalaTest for unit and integration testing
  "org.scalatest" %% "scalatest" % "3.2.14" % Test
)




resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.jcenterRepo,
  Resolver.mavenCentral
)

Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case PathList("org", "apache", "LLM_Generation/hadoop", "thirdparty", "google", "protobuf", xs @ _*) => MergeStrategy.first
  case x => MergeStrategy.defaultMergeStrategy(x)
}


// Merge strategy to handle potential conflicts during assembly
assembly / assemblyMergeStrategy := {
  // Discard various LICENSE and META-INF files
  case PathList("META-INF", "LICENSE") => MergeStrategy.discard
  case PathList("META-INF", "License") => MergeStrategy.discard
  case PathList("META-INF", "LICENSE.txt") => MergeStrategy.discard
  case PathList("META-INF", "LICENSE.TXT") => MergeStrategy.discard
  case PathList("META-INF", "license.txt") => MergeStrategy.discard
  case PathList("module-info.class") => MergeStrategy.discard

  // Handle specific files
  case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.first
  case PathList("reference.conf") => MergeStrategy.concat

  // Handle services and MANIFEST files within META-INF
  case PathList("META-INF", xs @ _*) =>
    xs match {
      case "MANIFEST.MF" :: Nil => MergeStrategy.discard
      case "services" :: _ => MergeStrategy.concat
      case _ => MergeStrategy.discard
    }

  // Handle specific packages and file extensions
  case PathList("META-INF", "services", xs @ _*) => MergeStrategy.concat
  case PathList("javax", "xml", xs @ _*) => MergeStrategy.first
  case PathList("com", "fasterxml", xs @ _*) => MergeStrategy.first

  // Handle files based on their extensions
  case PathList(ps @ _*) if ps.last endsWith ".class" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.rename
  case x if x.endsWith(".proto") => MergeStrategy.rename


  // Discard files containing "hadoop"
  case x if x.contains("LLM_Generation/hadoop") => MergeStrategy.discard

  // Default to keeping the first occurrence
  case _ => MergeStrategy.first
}

