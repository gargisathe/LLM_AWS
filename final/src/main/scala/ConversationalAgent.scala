//import akka.actor.ActorSystem
//import akka.http.scaladsl.Http
//import akka.http.scaladsl.model._
//import akka.http.scaladsl.model.HttpMethods._
//import akka.http.scaladsl.model.headers.RawHeader
//import akka.stream.ActorMaterializer
//import akka.util.ByteString
//
//import scala.concurrent.Future
//import scala.io.StdIn
//import com.google.protobuf.ByteString
//import lambda.lambda.{GenerateRequest, GenerateResponse}
//import spray.json.DefaultJsonProtocol.StringJsonFormat
//import spray.json._
//
//import scala.concurrent.duration.DurationInt  // To parse JSON response
//
//object ConversationalAgent {
//
//  implicit val system: ActorSystem = ActorSystem("conversation-system")
//  implicit val materializer: ActorMaterializer = ActorMaterializer()
//  implicit val executionContext = system.dispatcher
//
//  // Define the custom media type for gRPC + Proto
//  val applicationGrpcProto: MediaType.WithFixedCharset =
//    MediaType.customWithFixedCharset("application", "grpc+proto", HttpCharsets.`UTF-8`)
//
//  // Define the route to send Protobuf request to the microservice
//  def sendToMicroservice(prompt: String): Future[HttpResponse] = {
//    // Create the GenerateRequest Protobuf message
//    val generateRequest = GenerateRequest(prompt = prompt)
//
//    // Serialize the request to a byte array
//    val requestBytes = generateRequest.toByteArray
//
//    // Define the microservice URL (your service that sends requests to Lambda)
//    val microserviceUrl = "http://localhost:8081/invokeLambda"  // Replace with your actual microservice URL
//
//    // Construct the HTTP request to forward to the microservice
//    val httpRequest = HttpRequest(
//      method = POST,
//      uri = microserviceUrl,
//      entity = HttpEntity(applicationGrpcProto, requestBytes)
//    )
//
//    // Send the request to the microservice
//    Http().singleRequest(httpRequest)
//  }
//
//  // Handle the JSON response
////  def handleResponse(response: HttpResponse): Future[String] = {
////    // Read the response body and parse JSON
////    response.entity.toStrict(5.seconds).map { entity =>
////      // Parse the JSON response body
////      val jsonString = entity.data.utf8String
////      val jsonResponse = jsonString.parseJson.asJsObject
////      // Extract the "generated_text" from the response
////      val generatedText = jsonResponse.fields("generatedText").convertTo[String]
////      generatedText
////    }
////  }
//
//  def handleResponse(response: HttpResponse): Future[String] = {
//    // Read the response body and parse JSON
//    response.entity.toStrict(5.seconds).map { entity =>
//      // Log the raw JSON string for debugging
//      val jsonString = entity.data.utf8String
//      println(s"Raw JSON response: $jsonString") // Log the raw response
//
//      // Parse the JSON response body
//      try {
//        val jsonResponse = jsonString.parseJson.asJsObject
//        println(s"Parsed JSON response: $jsonResponse") // Log the parsed JSON
//
//        // Attempt to extract the expected field; adjust the field name if necessary
//        val generatedText = jsonResponse.fields.get("generated_text") match {
//          case Some(JsString(text)) => text
//          case _ => "Field 'generated_text' not found in response."
//        }
//        generatedText
//      } catch {
//        case ex: Exception =>
//          println(s"Error parsing JSON response: ${ex.getMessage}")
//          "Error parsing response."
//      }
//    }
//  }
//
//
//  // Main conversational loop
//  def main(args: Array[String]): Unit = {
//    // Starting the conversation loop
//    println("Welcome to the Conversational Agent!")
//    var continueConversation = true
//
//    while (continueConversation) {
//      // Read user input
//      println("\nPlease enter your message:")
//      val userInput = StdIn.readLine()
//
//      if (userInput.toLowerCase == "exit") {
//        println("Ending conversation. Goodbye!")
//        continueConversation = false
//      } else {
//        // Send the user input to the microservice
//        val responseFuture = sendToMicroservice(userInput)
//
//        // Process the response
//        responseFuture.onComplete {
//          case scala.util.Success(response) =>
//            handleResponse(response).onComplete {
//              case scala.util.Success(generatedText) =>
//                println(s"Microservice Response: $generatedText")
//              case scala.util.Failure(exception) =>
//                println(s"Error processing microservice response: ${exception.getMessage}")
//            }
//
//          case scala.util.Failure(exception) =>
//            println(s"Error sending request to microservice: ${exception.getMessage}")
//        }
//      }
//    }
//
//    // Wait for the future to complete (to keep the application alive)
//    StdIn.readLine()
//    system.terminate()
//  }
//}

//
//import akka.actor.ActorSystem
//import akka.http.scaladsl.Http
//import akka.http.scaladsl.model._
//import akka.http.scaladsl.model.HttpMethods._
//import akka.stream.ActorMaterializer
//import akka.util.ByteString
//import scala.concurrent.Future
//import scala.io.StdIn
//import com.google.protobuf.ByteString
//import lambda.lambda.{GenerateRequest, GenerateResponse}
//import spray.json.DefaultJsonProtocol.StringJsonFormat
//import spray.json._
//import scala.concurrent.duration.DurationInt  // To parse JSON response
//
//object ConversationalAgent {
//
//  implicit val system: ActorSystem = ActorSystem("conversation-system")
//  implicit val materializer: ActorMaterializer = ActorMaterializer()
//  implicit val executionContext = system.dispatcher
//
//  // Define the custom media type for gRPC + Proto
//  val applicationGrpcProto: MediaType.WithFixedCharset =
//    MediaType.customWithFixedCharset("application", "grpc+proto", HttpCharsets.`UTF-8`)
//
//  // Define the route to send Protobuf request to the microservice
//  def sendToMicroservice(prompt: String): Future[HttpResponse] = {
//    // Create the GenerateRequest Protobuf message
//    val generateRequest = GenerateRequest(prompt = prompt)
//
//    // Serialize the request to a byte array
//    val requestBytes = generateRequest.toByteArray
//
//    // Log the serialized Protobuf request data (you can log the byte array size)
//    println(s"Serialized Protobuf Request (size: ${requestBytes.length} bytes):")
//    println(requestBytes.map("%02X".format(_)).mkString(" ")) // Print the hex representation of the byte array
//
//    // Define the microservice URL (your service that sends requests to Lambda)
//    val microserviceUrl = "http://localhost:8081/invokeLambda"  // Replace with your actual microservice URL
//
//    // Construct the HTTP request to forward to the microservice
//    val httpRequest = HttpRequest(
//      method = POST,
//      uri = microserviceUrl,
//      entity = HttpEntity(applicationGrpcProto, requestBytes)
//    )
//
//    // Log the full HTTP request for debugging
//    println(s"HTTP Request to $microserviceUrl")
//    println(s"Method: ${httpRequest.method}")
//    println(s"URI: ${httpRequest.uri}")
//    println(s"Headers: ${httpRequest.headers}")
//    println(s"Entity: ${httpRequest.entity}")
//
//    // Send the request to the microservice
//    Http().singleRequest(httpRequest)
//  }
//
//  // Handle the JSON response
//  def handleResponse(response: HttpResponse): Future[String] = {
//    // Read the response body and parse JSON
//    response.entity.toStrict(5.seconds).map { entity =>
//      // Log the raw JSON string for debugging
//      val jsonString = entity.data.utf8String
//      println(s"Raw JSON response: $jsonString") // Log the raw response
//
//      // Parse the JSON response body
//      try {
//        val jsonResponse = jsonString.parseJson.asJsObject
//        println(s"Parsed JSON response: $jsonResponse") // Log the parsed JSON
//
//        // Attempt to extract the expected field; adjust the field name if necessary
//        val generatedText = jsonResponse.fields.get("generated_text") match {
//          case Some(JsString(text)) => text
//          case _ => "Field 'generated_text' not found in response."
//        }
//        generatedText
//      } catch {
//        case ex: Exception =>
//          println(s"Error parsing JSON response: ${ex.getMessage}")
//          "Error parsing response."
//      }
//    }
//  }
//
//  // Main conversational loop
//  def main(args: Array[String]): Unit = {
//    // Starting the conversation loop
//    println("Welcome to the Conversational Agent!")
//    var continueConversation = true
//
//    while (continueConversation) {
//      // Read user input
//      println("\nPlease enter your message:")
//      val userInput = StdIn.readLine()
//
//      if (userInput.toLowerCase == "exit") {
//        println("Ending conversation. Goodbye!")
//        continueConversation = false
//      } else {
//        // Send the user input to the microservice
//        val responseFuture = sendToMicroservice(userInput)
//
//        // Process the response
//        responseFuture.onComplete {
//          case scala.util.Success(response) =>
//            handleResponse(response).onComplete {
//              case scala.util.Success(generatedText) =>
//                println(s"Microservice Response: $generatedText")
//              case scala.util.Failure(exception) =>
//                println(s"Error processing microservice response: ${exception.getMessage}")
//            }
//
//          case scala.util.Failure(exception) =>
//            println(s"Error sending request to microservice: ${exception.getMessage}")
//        }
//      }
//    }
//
//    // Wait for the future to complete (to keep the application alive)
//    StdIn.readLine()
//    system.terminate()
//  }
//}

//import akka.actor.ActorSystem
//import akka.http.scaladsl.Http
//import akka.http.scaladsl.model._
//import akka.http.scaladsl.model.HttpMethods._
//import akka.stream.ActorMaterializer
//import akka.util.ByteString
//import com.google.protobuf.ByteString
//import lambda.lambda.{GenerateRequest, GenerateResponse}
//import spray.json.JsString
//
//import java.util.Base64
//import scala.concurrent.Future
//import scala.io.StdIn
//import scala.concurrent.duration.DurationInt
//
//object ConversationalAgent {
//
//  implicit val system: ActorSystem = ActorSystem("conversation-system")
//  implicit val materializer: ActorMaterializer = ActorMaterializer()
//  implicit val executionContext = system.dispatcher
//
//  // Define the custom media type for gRPC + Proto
//  val applicationGrpcProto: MediaType.WithFixedCharset =
//    MediaType.customWithFixedCharset("application", "grpc+proto", HttpCharsets.`UTF-8`)
//
//  // Define the route to send Base64-encoded Protobuf request to the microservice
//  import akka.http.scaladsl.model._
//  import akka.http.scaladsl.model.HttpMethods._
//  import com.google.protobuf.ByteString
//  import lambda.lambda.{GenerateRequest, GenerateResponse}
//  import spray.json._
//  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._  // for marshalling support
//
//  // Define a case class for the request body to send the Base64 string in JSON format
//  case class Base64Request(base64encodedRequest: String)
//
//  object Base64RequestProtocol extends DefaultJsonProtocol {
//    implicit val base64RequestFormat = jsonFormat1(Base64Request)
//  }
//
//  import Base64RequestProtocol._
//
//  // Sanitize the Base64 string to remove spaces and newlines
//  def sanitizeBase64String(base64String: String): String = {
//    base64String.replaceAll("\\s", "")  // Removes spaces and newlines
//  }
//
//  // Function to send Protobuf request to the microservice with sanitized Base64 string
//  def sendToMicroservice(prompt: String): Future[HttpResponse] = {
//    // Create the GenerateRequest Protobuf message
//    val generateRequest = GenerateRequest(prompt = prompt)
//
//    // Serialize the request to a byte array
//    val requestBytes = generateRequest.toByteArray
//
//    // Log the serialized Protobuf request data (you can log the byte array size)
//    println(s"Serialized Protobuf Request (size: ${requestBytes.length} bytes):")
//    println(requestBytes.map("%02X".format(_)).mkString(" ")) // Print the hex representation of the byte array
//
//    // Encode the Protobuf request to Base64
//    val base64EncodedRequest = java.util.Base64.getEncoder.encodeToString(requestBytes)
//
//    // Sanitize the Base64 string (remove unwanted spaces/newlines)
//    val sanitizedBase64EncodedRequest = sanitizeBase64String(base64EncodedRequest)
//
//    // Log the sanitized Base64 encoded Protobuf request
//    println(s"Base64 Encoded Protobuf Request (size: ${sanitizedBase64EncodedRequest.length} characters):")
//    println(sanitizedBase64EncodedRequest)
//
//    // Define the microservice URL (your service that sends requests to Lambda)
//    val microserviceUrl = "http://localhost:8081/invokeLambda"  // Replace with your actual microservice URL
//
//    // Construct the HTTP request to forward to the microservice
//    val httpRequest = HttpRequest(
//      method = POST,
//      uri = microserviceUrl,
//      entity = HttpEntity(ContentTypes.`application/json`, sanitizedBase64EncodedRequest)  // Send the sanitized Base64 string as the request body
//    )
//
//    // Log the full HTTP request for debugging
//    println(s"HTTP Request to $microserviceUrl")
//    println(s"Method: ${httpRequest.method}")
//    println(s"URI: ${httpRequest.uri}")
//    println(s"Headers: ${httpRequest.headers}")
//    println(s"Entity: ${httpRequest.entity}")
//
//    // Send the request to the microservice
//    Http().singleRequest(httpRequest)
//  }
//
//
//
//  // Handle the JSON response
//  import java.util.Base64
//  import lambda.lambda.GenerateResponse
//
//  // Handle the Base64-encoded Protobuf response
//  def handleResponse(response: HttpResponse): Future[String] = {
//    // Read the response body and parse the Base64-encoded data
//    response.entity.toStrict(5.seconds).map { entity =>
//      // Log the raw Base64-encoded string for debugging
//      val base64String = entity.data.utf8String
//      println(s"Raw Base64-encoded response: $base64String") // Log the raw Base64 string
//
//      try {
//        // Decode the Base64 string to get the original Protobuf message as a byte array
//        val decodedBytes = Base64.getDecoder.decode(base64String)
//        println(s"Decoded Protobuf Response (size: ${decodedBytes.length} bytes)")
//
//        // Deserialize the Protobuf response
//        val generateResponse = GenerateResponse.parseFrom(decodedBytes)
//        println(s"Parsed Protobuf response: $generateResponse")
//
//        // Extract the generated text from the response
//        val generatedText = generateResponse.generatedText
//        println(s"Generated Text: $generatedText")
//
//        generatedText
//      } catch {
//        case ex: Exception =>
//          println(s"Error decoding or parsing Protobuf response: ${ex.getMessage}")
//          "Error parsing response."
//      }
//    }
//  }
//
//
//  // Main conversational loop
//  def main(args: Array[String]): Unit = {
//    // Starting the conversation loop
//    println("Welcome to the Conversational Agent!")
//    var continueConversation = true
//
//    while (continueConversation) {
//      // Read user input
//      println("\nPlease enter your message:")
//      val userInput = StdIn.readLine()
//
//      if (userInput.toLowerCase == "exit") {
//        println("Ending conversation. Goodbye!")
//        continueConversation = false
//      } else {
//        // Send the user input to the microservice
//        val responseFuture = sendToMicroservice(userInput)
//
//        // Process the response
//        responseFuture.onComplete {
//          case scala.util.Success(response) =>
//            handleResponse(response).onComplete {
//              case scala.util.Success(generatedText) =>
//                println(s"Microservice Response: $generatedText")
//              case scala.util.Failure(exception) =>
//                println(s"Error processing microservice response: ${exception.getMessage}")
//            }
//
//          case scala.util.Failure(exception) =>
//            println(s"Error sending request to microservice: ${exception.getMessage}")
//        }
//      }
//    }
//
//    // Wait for the future to complete (to keep the application alive)
//    StdIn.readLine()
//    system.terminate()
//  }
//}


import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.google.protobuf.ByteString
import lambda.lambda.{GenerateRequest, GenerateResponse}
import spray.json.JsString
import org.slf4j.LoggerFactory

import java.util.Base64
import scala.concurrent.Future
import scala.io.StdIn
import scala.concurrent.duration._

object ConversationalAgent {

  // Load configuration
  val config = com.typesafe.config.ConfigFactory.load()

  // Logger setup
  val logger = LoggerFactory.getLogger(getClass)

  implicit val system: ActorSystem = ActorSystem("conversation-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  // Define the custom media type for gRPC + Proto
  val applicationGrpcProto: MediaType.WithFixedCharset =
    MediaType.customWithFixedCharset("application", "grpc+proto", HttpCharsets.`UTF-8`)

  // Read configuration values from application.conf
  val microserviceUrl: String = config.getString("conversationalAgent.microserviceUrl")
  val requestTimeout: FiniteDuration = config.getDuration("conversationalAgent.requestTimeout").toMillis.millis

  // Define a case class for the request body to send the Base64 string in JSON format
  case class Base64Request(base64encodedRequest: String)

  object Base64RequestProtocol extends DefaultJsonProtocol {
    implicit val base64RequestFormat = jsonFormat1(Base64Request)
  }

  import Base64RequestProtocol._

  // Sanitize the Base64 string to remove spaces and newlines
  def sanitizeBase64String(base64String: String): String = {
    base64String.replaceAll("\\s", "")  // Removes spaces and newlines
  }

  // Function to send Protobuf request to the microservice with sanitized Base64 string
  def sendToMicroservice(prompt: String): Future[HttpResponse] = {
    // Create the GenerateRequest Protobuf message
    val generateRequest = GenerateRequest(prompt = prompt)

    // Serialize the request to a byte array
    val requestBytes = generateRequest.toByteArray

    // Encode the Protobuf request to Base64
    val base64EncodedRequest = java.util.Base64.getEncoder.encodeToString(requestBytes)

    // Sanitize the Base64 string (remove unwanted spaces/newlines)
    val sanitizedBase64EncodedRequest = sanitizeBase64String(base64EncodedRequest)

    // Log the request data
    logger.info(s"Serialized Protobuf Request (size: ${requestBytes.length} bytes): ${requestBytes.map("%02X".format(_)).mkString(" ")}")
    logger.info(s"Base64 Encoded Protobuf Request (size: ${sanitizedBase64EncodedRequest.length} characters): $sanitizedBase64EncodedRequest")

    // Construct the HTTP request to forward to the microservice
    val httpRequest = HttpRequest(
      method = HttpMethods.POST,
      uri = microserviceUrl,
      entity = HttpEntity(ContentTypes.`application/json`, sanitizedBase64EncodedRequest)  // Send the sanitized Base64 string as the request body
    )

    // Log the HTTP request details
    logger.debug(s"HTTP Request to $microserviceUrl")
    logger.debug(s"Method: ${httpRequest.method}")
    logger.debug(s"URI: ${httpRequest.uri}")
    logger.debug(s"Headers: ${httpRequest.headers}")
    logger.debug(s"Entity: ${httpRequest.entity}")

    // Send the request to the microservice
    Http().singleRequest(httpRequest)
  }

  // Handle the Base64-encoded Protobuf response
  def handleResponse(response: HttpResponse): Future[String] = {
    response.entity.toStrict(requestTimeout).map { entity =>
      val base64String = entity.data.utf8String

      // Log the raw Base64-encoded response
      logger.debug(s"Raw Base64-encoded response: $base64String")

      try {
        // Decode the Base64 string to get the original Protobuf message as a byte array
        val decodedBytes = Base64.getDecoder.decode(base64String)
        logger.debug(s"Decoded Protobuf Response (size: ${decodedBytes.length} bytes)")

        // Deserialize the Protobuf response
        val generateResponse = GenerateResponse.parseFrom(decodedBytes)
        logger.info(s"Parsed Protobuf response: $generateResponse")

        // Extract the generated text from the response
        val generatedText = generateResponse.generatedText
        generatedText
      } catch {
        case ex: Exception =>
          logger.error(s"Error decoding or parsing Protobuf response: ${ex.getMessage}")
          "Error parsing response."
      }
    }
  }

  // Main conversational loop
  def main(args: Array[String]): Unit = {
    // Starting the conversation loop
    logger.info("Welcome to the Conversational Agent!")
    var continueConversation = true

    while (continueConversation) {
      // Read user input
      println("\nPlease enter your message:")
      val userInput = StdIn.readLine()

      if (userInput.toLowerCase == "exit") {
        logger.info("Ending conversation. Goodbye!")
        continueConversation = false
      } else {
        // Send the user input to the microservice
        val responseFuture = sendToMicroservice(userInput)

        // Process the response
        responseFuture.onComplete {
          case scala.util.Success(response) =>
            handleResponse(response).onComplete {
              case scala.util.Success(generatedText) =>
                logger.info(s"Microservice Response: $generatedText")
              case scala.util.Failure(exception) =>
                logger.error(s"Error processing microservice response: ${exception.getMessage}")
            }

          case scala.util.Failure(exception) =>
            logger.error(s"Error sending request to microservice: ${exception.getMessage}")
        }
      }
    }

    // Wait for the future to complete (to keep the application alive)
    StdIn.readLine()
    system.terminate()
  }
}
