import scala.collection

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.13"

lazy val root = (project in file("."))
  .settings(
    name := "Lambda"
  )


libraryDependencies  ++= Seq(
  // Akka HTTP for RESTful API
  "com.typesafe.akka" %% "akka-http" % "10.2.10",
  "com.typesafe.akka" %% "akka-stream" % "2.6.21",
  "com.typesafe.akka" %% "akka-actor" % "2.6.21",
  "io.spray" %% "spray-json" % "1.3.6",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.10",



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

  // protobuf
  "com.google.protobuf" % "protobuf-java" % "3.19.4",



)

Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
)


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


