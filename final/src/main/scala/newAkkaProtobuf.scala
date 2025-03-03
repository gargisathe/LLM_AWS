//import LLM_Interaction.AWS.InterfaceAWS
//import akka.actor.ActorSystem
//import akka.http.scaladsl.Http
//import akka.http.scaladsl.server.Directives._
//import akka.stream.ActorMaterializer
//import akka.http.scaladsl.model._
//import akka.http.scaladsl.model.StatusCodes._
//import lambda.lambda.GenerateRequest
//import lambda.lambda.GenerateResponse
//import com.google.protobuf.ByteString
//
//import scala.concurrent.Future
//import scala.concurrent.ExecutionContext.Implicits.global
//import akka.http.scaladsl.model.ContentTypes._
//import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
//
//object LLMApiServer {
//
//  implicit val system: ActorSystem = ActorSystem("llm-api-system")
//  implicit val materializer: ActorMaterializer = ActorMaterializer()
//  implicit val executionContext = system.dispatcher
//
//  // Define the routes
//  val route =
//    path("generate") {
//      post {
//        entity(as[String]) { prompt =>
//          // Process the request and generate text using your LLM
//          val generatedTextFuture: Future[String] = generateTextFromPrompt(prompt)
//
//          onComplete(generatedTextFuture) {
//            case scala.util.Success(generatedText) =>
//              // Create the GenerateResponse Protobuf object
//              val response = GenerateResponse(generatedText = generatedText)
//              val responseBytes = response.toByteArray  // Serialize Protobuf message to byte array
//
//              // Return the response as Protobuf (binary format) with the correct content type
//              complete(HttpResponse(entity = HttpEntity(`application/grpc+proto`, responseBytes)))
//
//            case scala.util.Failure(exception) =>
//              complete(HttpResponse(InternalServerError, entity = s"Error: ${exception.getMessage}"))
//          }
//        }
//      }
//    }
//
//  // Function to generate text from the prompt
//  def generateTextFromPrompt(prompt: String): Future[String] = {
//    Future {
//      // Tokenize, generate embeddings, apply sliding window, and predict next words
//      // Call InterfaceAWS methods here
//      val tokensWithWords = InterfaceAWS.tokenize(prompt)
//      val embeddings = InterfaceAWS.generateEmbeddings(tokensWithWords)
//      val slidingWindows = InterfaceAWS.applySlidingWindow(embeddings)
//      val predictedWords = InterfaceAWS.predictNextWords(slidingWindows, InterfaceAWS.tmpNeuralModelPath, WordVectorSerializer.readWord2VecModel(InterfaceAWS.tmpWord2VecModelPath), InterfaceAWS.loadTokenIdToWordMap())
//
//      // Combine the predicted words into a string
//      predictedWords.mkString(" ")
//    }
//  }
//
//  def main(args: Array[String]): Unit = {
//    // Bind the server to localhost and port 8080
//    Http().newServerAt("0.0.0.0", 8080).bind(route).map { binding =>
//      println(s"Server is running at ${binding.localAddress}")
//    }.recover {
//      case ex: Exception => println(s"Failed to bind HTTP server: ${ex.getMessage}")
//    }
//  }
//}
//


import LLM_Interaction.AWS.InterfaceAWS
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.StatusCodes._
import lambda.lambda.GenerateRequest
import lambda.lambda.GenerateResponse
import com.google.protobuf.ByteString
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.model.ContentTypes._
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
//
//object LLMApiServer {
//
//  implicit val system: ActorSystem = ActorSystem("llm-api-system")
//  implicit val materializer: ActorMaterializer = ActorMaterializer()
//  implicit val executionContext = system.dispatcher
//
//  // Define the routes
//  val route =
//    path("generate") {
//      post {
//        entity(as[Array[Byte]]) { requestBytes =>
//          // Deserialize the incoming Protobuf message to GenerateRequest
//          val request = GenerateRequest.parseFrom(requestBytes)
//
//          // Assuming the GenerateRequest does not need a prompt, process accordingly
//          val generatedTextFuture: Future[String] = generateTextFromPrompt()
//
//          onComplete(generatedTextFuture) {
//            case scala.util.Success(generatedText) =>
//              // Create the GenerateResponse Protobuf object
//              val response = GenerateResponse(generatedText = generatedText)
//              val responseBytes = response.toByteArray  // Serialize Protobuf message to byte array
//
//              // Return the response as Protobuf (binary format) with the correct content type
//              complete(HttpResponse(entity = HttpEntity(`application/grpc+proto`, responseBytes)))
//
//            case scala.util.Failure(exception) =>
//              complete(HttpResponse(InternalServerError, entity = s"Error: ${exception.getMessage}"))
//          }
//        }
//      }
//    }
//
//  // Function to generate text from some internal logic (no prompt needed)
//  def generateTextFromPrompt(): Future[String] = {
//    Future {
//      // Tokenize, generate embeddings, apply sliding window, and predict next words
//      // Call InterfaceAWS methods here (remove prompt-related logic)
//      val tokensWithWords = InterfaceAWS.tokenize("default input")  // Or your new logic here
//      val embeddings = InterfaceAWS.generateEmbeddings(tokensWithWords)
//      val slidingWindows = InterfaceAWS.applySlidingWindow(embeddings)
//      val predictedWords = InterfaceAWS.predictNextWords(slidingWindows, InterfaceAWS.tmpNeuralModelPath, WordVectorSerializer.readWord2VecModel(InterfaceAWS.tmpWord2VecModelPath), InterfaceAWS.loadTokenIdToWordMap())
//
//      // Combine the predicted words into a string
//      predictedWords.mkString(" ")
//    }
//  }
//
//  def main(args: Array[String]): Unit = {
//    // Bind the server to localhost and port 8080
//    Http().newServerAt("0.0.0.0", 8080).bind(route).map { binding =>
//      println(s"Server is running at ${binding.localAddress}")
//    }.recover {
//      case ex: Exception => println(s"Failed to bind HTTP server: ${ex.getMessage}")
//    }
//  }
//}

import LLM_Interaction.AWS.InterfaceAWS
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.StatusCodes._
import lambda.lambda.GenerateRequest
import lambda.lambda.GenerateResponse
import com.google.protobuf.ByteString
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.model.ContentTypes._
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

object LLMApiServer {

  val logger = LoggerFactory.getLogger("newAkkaProtobuf")

  // Load configuration values from application.conf
  val config = ConfigFactory.load()
  val host = config.getString("server.host")
  val port = config.getInt("server.port")
  val grpcContentType = config.getString("grpc.content-type")

  implicit val system: ActorSystem = ActorSystem("llm-api-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  // Define the routes
  val route =
    path("generate") {
      post {
        entity(as[Array[Byte]]) { requestBytes =>
          // Log the incoming request bytes
          logger.debug(s"Received request bytes (size: ${requestBytes.length} bytes): ${requestBytes.map("%02X".format(_)).mkString(" ")}")

          // Deserialize the incoming Protobuf message to GenerateRequest
          val request = GenerateRequest.parseFrom(requestBytes)

          // Extract the prompt from the deserialized request
          val prompt = request.prompt
          logger.debug(s"Extracted prompt: $prompt")

          // Process the request and generate text using your LLM
          val generatedTextFuture: Future[String] = generateTextFromPrompt(prompt)

          onComplete(generatedTextFuture) {
            case scala.util.Success(generatedText) =>
              // Log the generated text
              logger.debug(s"Generated text: $generatedText")

              // Create the GenerateResponse Protobuf object
              val response = GenerateResponse(generatedText = generatedText)
              val responseBytes = response.toByteArray  // Serialize Protobuf message to byte array

              // Return the response as Protobuf (binary format) with the correct content type
              complete(HttpResponse(entity = HttpEntity(grpcContentType, responseBytes)))

            case scala.util.Failure(exception) =>
              // Log the error
              logger.error(s"Error during text generation: ${exception.getMessage}")
              complete(HttpResponse(InternalServerError, entity = s"Error: ${exception.getMessage}"))
          }
        }
      }
    }

  // Function to generate text from the prompt
  def generateTextFromPrompt(prompt: String): Future[String] = {
    Future {
      // Tokenize, generate embeddings, apply sliding window, and predict next words
      // Call InterfaceAWS methods here
      logger.debug(s"Tokenizing prompt: $prompt")
      val tokensWithWords = InterfaceAWS.tokenize(prompt)
      val embeddings = InterfaceAWS.generateEmbeddings(tokensWithWords)
      val slidingWindows = InterfaceAWS.applySlidingWindow(embeddings)
      val predictedWords = InterfaceAWS.predictNextWords(slidingWindows, InterfaceAWS.tmpNeuralModelPath, WordVectorSerializer.readWord2VecModel(InterfaceAWS.tmpWord2VecModelPath), InterfaceAWS.loadTokenIdToWordMap())

      // Combine the predicted words into a string
      val result = predictedWords.mkString(" ")
      logger.debug(s"Predicted words: $result")
      result
    }
  }

  def main(args: Array[String]): Unit = {
    // Bind the server to the configured host and port
    Http().newServerAt(host, port).bind(route).map { binding =>
      logger.info(s"Server is running at ${binding.localAddress}")
    }.recover {
      case ex: Exception => logger.error(s"Failed to bind HTTP server: ${ex.getMessage}")
    }
  }
}
