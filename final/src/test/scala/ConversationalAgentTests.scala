import org.scalatest._
import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.Http
import scala.concurrent.Future
import scala.util.{Success, Failure}
import spray.json._

class ConversationalAgentTests extends FlatSpec with Matchers {

  implicit val system: ActorSystem = ActorSystem("test-system")
  implicit val executionContext = system.dispatcher
  val testAgent = new ConversationalAgent.type

  "Configuration loading" should "load microserviceUrl and requestTimeout correctly" in {
    val microserviceUrl = testAgent.microserviceUrl
    val requestTimeout = testAgent.requestTimeout

    assert(microserviceUrl == "http://localhost:8081/invokeLambda")
    assert(requestTimeout == 5000.millis)
  }

  "sanitizeBase64String" should "remove spaces and newlines from the Base64 string" in {
    val input = "dGVzdCBzdHJpbmcgYXNzb2NpYXRlZCB3aXRoIG5ld3NsYW5ndWFnYWdlIGNvbnRlbnQ="
    val sanitized = testAgent.sanitizeBase64String(input)
    assert(sanitized == "dGVzdCBzdHJpbmcgYXNzb2NpYXRlZCB3aXRoIG5ld3NsYW5ndWFnYWdlIGNvbnRlbnQ=")
  }

  "sendToMicroservice" should "send a correctly formatted HTTP request to the microservice" in {
    val responseFuture: Future[HttpResponse] = testAgent.sendToMicroservice("test prompt")
    responseFuture.onComplete {
      case Success(response) =>
        assert(response.status == StatusCodes.OK)
        println(s"Sent HTTP request successfully: ${response}")
      case Failure(exception) =>
        fail(s"Failed to send HTTP request: ${exception.getMessage}")
    }
  }

  "handleResponse" should "decode and parse the Protobuf response correctly" in {
    val base64Response = "your_base64_encoded_protobuf_response_here"
    val mockResponse = HttpResponse(entity = HttpEntity(base64Response))

    val responseText = testAgent.handleResponse(mockResponse).await
    assert(responseText == "Expected generated text from Protobuf response")
  }

  "Conversational loop" should "process user input and generate microservice responses" in {
    // Simulate user input
    val userInput = "Hello, how are you?"
    val responseFuture = testAgent.sendToMicroservice(userInput)

    responseFuture.onComplete {
      case Success(response) =>
        testAgent.handleResponse(response).onComplete {
          case Success(generatedText) =>
            assert(generatedText.nonEmpty)
            println(s"Response: $generatedText")
          case Failure(exception) =>
            fail(s"Failed to process response: ${exception.getMessage}")
        }
      case Failure(exception) =>
        fail(s"Failed to send request: ${exception.getMessage}")
    }
  }

  "System error" should "handle errors properly and log error messages" in {
    val invalidUrl = "http://invalid-url"
    val responseFuture = testAgent.sendToMicroservice("Invalid URL test")

    responseFuture.onComplete {
      case Success(response) =>
        fail("Expected failure due to invalid URL")
      case Failure(exception) =>
        println(s"Expected error: ${exception.getMessage}")
        assert(exception.getMessage.contains("Error sending request to microservice"))
    }
  }
}
