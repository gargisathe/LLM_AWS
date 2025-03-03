import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import java.net.URL
import java.io.InputStreamReader
import java.net.HttpURLConnection
import lambda.{GenerateRequest, GenerateResponse}
import scala.util.{Failure, Success, Try}

import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}

import java.util.Base64
import scala.util.{Failure, Success, Try}

object LambdaHandler {

  // HTTP client to interact with EC2 instance
  def lambda_function(prompt: String): String = {
    val url = new URL("http://13.55.48.156:8080/generate") // EC2 URL where LLM is hosted
    val connection = url.openConnection().asInstanceOf[HttpURLConnection]
    connection.setRequestMethod("POST")
    connection.setDoOutput(true)

    val inputJson = s"""{"prompt": "$prompt"}"""
    val os = connection.getOutputStream
    os.write(inputJson.getBytes("UTF-8"))
    os.close()

    val responseCode = connection.getResponseCode
    if (responseCode == HttpURLConnection.HTTP_OK) {
      val in = new InputStreamReader(connection.getInputStream)
      val content = scala.io.Source.fromInputStream(connection.getInputStream).mkString
      content
    } else {
      throw new Exception(s"Failed to communicate with EC2, response code: $responseCode")
    }
  }
}

class LambdaFunction extends RequestHandler[Array[Byte], Array[Byte]] {

  // Method to handle Lambda invocation
  override def handleRequest(input: Array[Byte], context: Context): Array[Byte] = {

    // Decode the incoming Base64-encoded Protobuf data
    val decodedInput = Try(Base64.getDecoder.decode(input)) match {
      case Success(decoded) => decoded
      case Failure(ex) => throw new RuntimeException(s"Failed to decode Base64 input: ${ex.getMessage}")
    }

    // Deserialize the Protobuf request
    val generateRequest = GenerateRequest.parseFrom(decodedInput)

    // Send the prompt to the LLM on EC2 and get the response
    val responseText = Try(LambdaHandler.lambda_function(generateRequest.prompt)) match {
      case Success(response) => response
      case Failure(ex) => s"Error: ${ex.getMessage}"
    }

    // Create the response Protobuf message
    val generateResponse = GenerateResponse(generatedText = responseText)

    // Serialize the response to Protobuf format and return it
    generateResponse.toByteArray
  }
}
