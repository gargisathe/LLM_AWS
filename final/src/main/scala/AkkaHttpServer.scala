//import akka.actor.ActorSystem
//import akka.http.scaladsl.Http
//import akka.http.scaladsl.server.Directives._
//import akka.stream.ActorMaterializer
//import akka.http.scaladsl.model._
//import akka.http.scaladsl.model.StatusCodes._
//
//import lambda.lambda.GenerateRequest
//// Import generated Protobuf classes
//import akka.http.scaladsl.model._
//import akka.http.scaladsl.server.Directives._
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.Future
//import io.circe.generic.auto._
//import io.circe.syntax._
//import io.circe.parser.decode
//import LLM_Interaction.AWS.InterfaceAWS
//import lambda.lambda.GenerateResponse
//import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer  // Your LLM interaction package
//
//
//
//object LLMApiServer {
//
//  implicit val system: ActorSystem = ActorSystem("llm-api-system")
//  implicit val materializer: ActorMaterializer = ActorMaterializer()
//  implicit val executionContext = system.dispatcher
//
//  //Define the routes
//  val route =
//    path("generate") {
//      post {
//        entity(as[String]) { prompt =>
//          // Process the request and generate text using your LLM
//          val generatedTextFuture: Future[String] = generateTextFromPrompt(prompt)
//
//          onComplete(generatedTextFuture) {
//            case scala.util.Success(generatedText) =>
//              val response = GenerateResponse(generatedText = generatedText)
//              complete(HttpResponse(entity = HttpEntity(ContentTypes.`application/json`, response.asJson.noSpaces)))
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
