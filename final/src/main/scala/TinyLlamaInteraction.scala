package LLM_Interaction.localconversation

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import spray.json._
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import com.typesafe.config.ConfigFactory

object TinyLlamaInteraction {

  // Logger setup
  val logger = LoggerFactory.getLogger(getClass)

  // Load configuration values from application.conf
  val config = ConfigFactory.load()
  val ollamaHost = config.getString("tinyllama.host")
  val tinyLlamaModel = config.getString("tinyllama.model")

  // JSON Parsing for Request and Response
  case class PromptInput(model: String, prompt: String)
  case class PromptResponse(model: String, response: String, done: Boolean)

  object JsonProtocol extends DefaultJsonProtocol {
    implicit val promptInputFormat = jsonFormat2(PromptInput)
    implicit val promptResponseFormat = jsonFormat3(PromptResponse)
  }

  import JsonProtocol._

  // Actor System and Execution Context
  implicit val system: ActorSystem = ActorSystem("TinyLlamaSystem")
  implicit val executionContext: ExecutionContext = system.dispatcher

  // Function to Process Streaming Responses
  def processStreamingResponse(response: HttpResponse): Future[String] = {
    logger.debug("Processing streaming response...")

    // Accumulate the response chunks
    response.entity.dataBytes
      .map(_.utf8String) // Convert ByteString to String
      .map { chunk =>
        // Parse each chunk as JSON and extract the "response" field
        chunk.parseJson.convertTo[PromptResponse].response
      }
      .runFold("")(_ + _) // Concatenate all chunks into a single string
      .map { fullResponse =>
        // Split the response into lines and take only the first two
        logger.debug(s"Received response: $fullResponse")
        fullResponse.split("\n").take(2).mkString("\n")
      }
  }

  // Function to Generate a Response
  def generateResponse(prompt: String): Future[String] = {
    logger.info(s"Generating response for prompt: $prompt")

    val modifiedPrompt = s"$prompt Please respond in exactly two lines."
    val requestEntity = HttpEntity(ContentTypes.`application/json`, PromptInput(tinyLlamaModel, modifiedPrompt).toJson.compactPrint)

    val responseFuture = Http().singleRequest(
      HttpRequest(
        method = HttpMethods.POST,
        uri = s"$ollamaHost/api/generate",
        entity = requestEntity
      )
    )

    // Process the streaming response
    responseFuture.flatMap(processStreamingResponse)
  }

  // Main Program
  def main(args: Array[String]): Unit = {
    logger.info("Starting TinyLlama Interaction Program")
    println("Direct REST TinyLlama Streaming Interaction Program")
    println("Enter your prompt (or type 'exit' to quit):")

    var continue = true
    while (continue) {
      print("> ")
      val userPrompt = scala.io.StdIn.readLine()

      userPrompt.toLowerCase match {
        case "exit" =>
          continue = false
          logger.info("Exiting program. Goodbye!")
          println("Exiting program. Goodbye!")
        case prompt =>
          val responseFuture = generateResponse(prompt)
          responseFuture.onComplete {
            case scala.util.Success(response) =>
              logger.info(s"Response: $response")
              println(s"TinyLlama Response: $response")
            case scala.util.Failure(exception) =>
              logger.error(s"Error: ${exception.getMessage}")
              println(s"Error: ${exception.getMessage}")
          }
          // Wait for the response before proceeding
          Await.ready(responseFuture, 30.seconds)
      }
    }

    // Shutdown the ActorSystem
    system.terminate()
  }
}
