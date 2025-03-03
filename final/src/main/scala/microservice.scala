//import akka.actor.ActorSystem
//import akka.http.scaladsl.Http
//import akka.http.scaladsl.server.Directives._
//import akka.stream.ActorMaterializer
//import akka.http.scaladsl.model._
//import akka.http.scaladsl.model.headers.RawHeader
//import akka.http.scaladsl.model.HttpCharsets
//import akka.util.ByteString
//
//
//import scala.concurrent.Future
//import com.google.protobuf.ByteString
//import lambda.lambda.{GenerateRequest, GenerateResponse}
//
//object LambdaForwardingService {
//
//  implicit val system: ActorSystem = ActorSystem("lambda-forwarding-system")
//  implicit val materializer: ActorMaterializer = ActorMaterializer()
//  implicit val executionContext = system.dispatcher
//
//  // Define the custom media type for gRPC + Proto
//  val applicationGrpcProto: MediaType.WithFixedCharset =
//    MediaType.customWithFixedCharset("application", "grpc+proto", HttpCharsets.`UTF-8`)
//
//  // Endpoint to handle user input and forward to AWS API Gateway
//  val route =
//    path("invokeLambda") {
//      post {
//        entity(as[Array[Byte]]) { requestBytes =>
//          // Deserialize the incoming Protobuf request
//          val generateRequest = GenerateRequest.parseFrom(requestBytes)
//
//          // Forward the request to Lambda via the API Gateway
//          val responseFuture: Future[HttpResponse] = forwardToLambda(generateRequest)
//
//          // Handle the response from Lambda
//          onComplete(responseFuture) {
//            case scala.util.Success(response) =>
//              val responseBytes = response.entity.dataBytes.runFold(ByteString.EMPTY)(_ ++ _).map(_.toByteArray)
//              complete(HttpEntity(applicationGrpcProto, responseBytes))
//
//            case scala.util.Failure(exception) =>
//              complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Error: ${exception.getMessage}"))
//          }
//        }
//      }
//    }
//
//  // Function to forward the request to AWS Lambda through the API Gateway
//  def forwardToLambda(request: GenerateRequest): Future[HttpResponse] = {
//    val apiUrl = "https://your-api-gateway-url/invoke"  // Replace with your actual API Gateway URL
//
//    // Serialize the request to a byte array
//    val requestBytes = request.toByteArray
//
//    // Construct the HTTP request to forward to Lambda
//    val httpRequest = HttpRequest(
//      method = HttpMethods.POST,
//      uri = apiUrl,
//      entity = HttpEntity(applicationGrpcProto, requestBytes)
//    ).withHeaders(
//      RawHeader("Content-Type", "application/grpc+proto"),
//      RawHeader("grpc-encoding", "identity"),
//      RawHeader("grpc-accept-encoding", "identity")
//    )
//
//    // Send the request to AWS API Gateway
//    Http().singleRequest(httpRequest)
//  }
//
//  // Main method to start the Akka HTTP server
//  def main(args: Array[String]): Unit = {
//    // Bind the Akka HTTP server to listen on port 8081
//    Http().newServerAt("0.0.0.0", 8081).bind(route).map { binding =>
//      println(s"Server is running at ${binding.localAddress}")
//    }.recover {
//      case ex: Exception => println(s"Failed to bind HTTP server: ${ex.getMessage}")
//    }
//  }
//}
//
//
//

//import akka.actor.ActorSystem
//import akka.http.scaladsl.Http
//import akka.http.scaladsl.server.Directives._
//import akka.stream.ActorMaterializer
//import akka.http.scaladsl.model._
//import akka.http.scaladsl.model.headers.RawHeader
//import akka.http.scaladsl.model.HttpCharsets
//import akka.util.ByteString
//import scala.concurrent.Future
//import lambda.lambda.{GenerateRequest, GenerateResponse}
//
//object LambdaForwardingService {
//
//  implicit val system: ActorSystem = ActorSystem("lambda-forwarding-system")
//  implicit val materializer: ActorMaterializer = ActorMaterializer()
//  implicit val executionContext = system.dispatcher
//
//  // Define the custom media type for gRPC + Proto
//  val applicationGrpcProto: MediaType.WithFixedCharset =
//    MediaType.customWithFixedCharset("application", "grpc+proto", HttpCharsets.`UTF-8`)
//
//  // Endpoint to handle user input and forward to AWS API Gateway
//  val route =
//    path("invokeLambda") {
//      post {
//        entity(as[Array[Byte]]) { requestBytes =>
//          // Deserialize the incoming Protobuf request
//          val generateRequest = GenerateRequest.parseFrom(requestBytes)
//
//          // Forward the request to Lambda via the API Gateway
//          val responseFuture: Future[HttpResponse] = forwardToLambda(generateRequest)
//
//          // Handle the response from Lambda
//          onComplete(responseFuture) {
//            case scala.util.Success(response) =>
//              // Combine the response body into a single ByteString
//              val combinedBytesFuture = response.entity.dataBytes.runFold(ByteString.empty)(_ ++ _)
//
//              // Use map to ensure the response is processed and returned as an HTTP entity
//              onComplete(combinedBytesFuture) {
//                case scala.util.Success(combinedBytes) =>
//                  complete(HttpEntity(applicationGrpcProto, combinedBytes))
//
//                case scala.util.Failure(exception) =>
//                  complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Error: ${exception.getMessage}"))
//              }
//
//            case scala.util.Failure(exception) =>
//              complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Error: ${exception.getMessage}"))
//          }
//        }
//      }
//    }
//
//  // Function to forward the request to AWS Lambda through the API Gateway
//  def forwardToLambda(request: GenerateRequest): Future[HttpResponse] = {
//    val apiUrl = "https://kfqwrmdi91.execute-api.us-east-1.amazonaws.com/invoke"  // Replace with your actual API Gateway URL
//
//    // Serialize the request to a byte array
//    val requestBytes = request.toByteArray
//
//    // Construct the HTTP request to forward to Lambda
//    val httpRequest = HttpRequest(
//      method = HttpMethods.POST,
//      uri = apiUrl,
//      entity = HttpEntity(applicationGrpcProto, requestBytes)
//    ).withHeaders(
//      RawHeader("Content-Type", "application/grpc+proto"),
//      RawHeader("grpc-encoding", "identity"),
//      RawHeader("grpc-accept-encoding", "identity")
//    )
//
//    // Send the request to AWS API Gateway
//    Http().singleRequest(httpRequest)
//  }
//
//  // Main method to start the Akka HTTP server
//  def main(args: Array[String]): Unit = {
//    // Bind the Akka HTTP server to listen on port 8081
//    Http().newServerAt("0.0.0.0", 8081).bind(route).map { binding =>
//      println(s"Server is running at ${binding.localAddress}")
//
//
//    }.recover {
//      case ex: Exception => println(s"Failed to bind HTTP server: ${ex.getMessage}")
//    }
//  }
//}

//correct
//import akka.actor.ActorSystem
//import akka.http.scaladsl.Http
//import akka.http.scaladsl.server.Directives._
//import akka.stream.ActorMaterializer
//import akka.http.scaladsl.model._
//import akka.http.scaladsl.model.headers.RawHeader
//import akka.http.scaladsl.model.HttpCharsets
//import akka.util.ByteString
//import scala.concurrent.Future
//import lambda.lambda.{GenerateRequest, GenerateResponse}
//
//object LambdaForwardingService {
//
//  implicit val system: ActorSystem = ActorSystem("lambda-forwarding-system")
//  implicit val materializer: ActorMaterializer = ActorMaterializer()
//  implicit val executionContext = system.dispatcher
//
//  // Define the custom media type for gRPC + Proto
//  val applicationGrpcProto: MediaType.WithFixedCharset =
//    MediaType.customWithFixedCharset("application", "grpc+proto", HttpCharsets.`UTF-8`)
//
//  // Endpoint to handle user input and forward to AWS API Gateway
//  val route =
//    path("invokeLambda") {
//      post {
//        entity(as[Array[Byte]]) { requestBytes =>
//          // Deserialize the incoming Protobuf request
//          val generateRequest = GenerateRequest.parseFrom(requestBytes)
//
//          // Forward the request to Lambda via the API Gateway
//          val responseFuture: Future[HttpResponse] = forwardToLambda(generateRequest)
//
//          // Handle the response from Lambda
//          onComplete(responseFuture) {
//            case scala.util.Success(response) =>
//              // Combine the response body into a single ByteString
//              val combinedBytesFuture = response.entity.dataBytes.runFold(ByteString.empty)(_ ++ _)
//
//              // Use map to ensure the response is processed and returned as an HTTP entity
//              onComplete(combinedBytesFuture) {
//                case scala.util.Success(combinedBytes) =>
//                  complete(HttpEntity(applicationGrpcProto, combinedBytes))
//
//                case scala.util.Failure(exception) =>
//                  complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Error: ${exception.getMessage}"))
//              }
//
//            case scala.util.Failure(exception) =>
//              complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Error: ${exception.getMessage}"))
//          }
//        }
//      }
//    }
//
//  // Function to forward the request to AWS Lambda through the API Gateway
//  def forwardToLambda(request: GenerateRequest): Future[HttpResponse] = {
//    val apiUrl = "https://0nuai7kiha.execute-api.ap-southeast-2.amazonaws.com/invoke/LLM_Lambda"  // Replace with your actual API Gateway URL
//
//    // Serialize the request to a byte array
//    val requestBytes = request.toByteArray
//
//    // Construct the HTTP request to forward to Lambda
//    val httpRequest = HttpRequest(
//      method = HttpMethods.POST,
//      uri = apiUrl,
//      entity = HttpEntity(applicationGrpcProto, requestBytes)
//    ).withHeaders(
//      RawHeader("Content-Type", "application/grpc+proto"),
//      RawHeader("grpc-encoding", "identity"),
//      RawHeader("grpc-accept-encoding", "identity")
//    )
//
//    // Send the request to AWS API Gateway
//    Http().singleRequest(httpRequest)
//  }
//
//  // Main method to start the Akka HTTP server
//  def main(args: Array[String]): Unit = {
//    // Bind the Akka HTTP server to listen on port 8081
//    val bindingFuture = Http().newServerAt("0.0.0.0", 8081).bind(route)
//
//    // Inform that the server is running
//    bindingFuture.map { binding =>
//      println(s"Server is running at ${binding.localAddress}")
//    }.recover {
//      case ex: Exception => println(s"Failed to bind HTTP server: ${ex.getMessage}")
//    }
//
//    // Wait for user input (Enter key) to stop the server
//    println("Press Enter to stop the server...")
//    scala.io.StdIn.readLine()  // Block and wait for Enter key
//
//    // Gracefully stop the server
//    bindingFuture.flatMap(_.unbind()).onComplete { _ =>
//      println("Server stopped.")
//      system.terminate()
//    }
//  }
//}

//also correct
//import akka.actor.ActorSystem
//import akka.http.scaladsl.Http
//import akka.http.scaladsl.server.Directives._
//import akka.stream.ActorMaterializer
//import akka.http.scaladsl.model._
//import akka.http.scaladsl.model.headers.RawHeader
//import akka.http.scaladsl.model.HttpCharsets
//import akka.util.ByteString
//import scala.concurrent.Future
//import lambda.lambda.{GenerateRequest, GenerateResponse}
//
//object LambdaForwardingService {
//
//  implicit val system: ActorSystem = ActorSystem("lambda-forwarding-system")
//  implicit val materializer: ActorMaterializer = ActorMaterializer()
//  implicit val executionContext = system.dispatcher
//
//  // Define the custom media type for gRPC + Proto
//  val applicationGrpcProto: MediaType.WithFixedCharset =
//    MediaType.customWithFixedCharset("application", "grpc+proto", HttpCharsets.`UTF-8`)
//
//  // Endpoint to handle user input and forward to AWS API Gateway
//  val route =
//    path("invokeLambda") {
//      post {
//        entity(as[Array[Byte]]) { requestBytes =>
//          // Log the incoming Protobuf request
//          println(s"Received request (size: ${requestBytes.length} bytes):")
//          println(requestBytes.map("%02X".format(_)).mkString(" "))  // Log the hex format of Protobuf data
//
//          // Deserialize the incoming Protobuf request
//          val generateRequest = GenerateRequest.parseFrom(requestBytes)
//
//          // Forward the request to Lambda via the API Gateway
//          val responseFuture: Future[HttpResponse] = forwardToLambda(generateRequest)
//
//          // Handle the response from Lambda
//          onComplete(responseFuture) {
//            case scala.util.Success(response) =>
//              // Combine the response body into a single ByteString
//              val combinedBytesFuture = response.entity.dataBytes.runFold(ByteString.empty)(_ ++ _)
//
//              // Use map to ensure the response is processed and returned as an HTTP entity
//              onComplete(combinedBytesFuture) {
//                case scala.util.Success(combinedBytes) =>
//                  complete(HttpEntity(applicationGrpcProto, combinedBytes))
//
//                case scala.util.Failure(exception) =>
//                  complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Error: ${exception.getMessage}"))
//              }
//
//            case scala.util.Failure(exception) =>
//              complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Error: ${exception.getMessage}"))
//          }
//        }
//      }
//    }
//
//  // Function to forward the request to AWS Lambda through the API Gateway
//  def forwardToLambda(request: GenerateRequest): Future[HttpResponse] = {
//    val apiUrl = "https://0nuai7kiha.execute-api.ap-southeast-2.amazonaws.com/invoke/LLM_Lambda"  // Replace with your actual API Gateway URL
//
//    // Serialize the request to a byte array
//    val requestBytes = request.toByteArray
//
//    // Log the Protobuf data that is being sent to the API Gateway
//    println(s"Forwarding request to API Gateway (size: ${requestBytes.length} bytes):")
//    println(requestBytes.map("%02X".format(_)).mkString(" "))  // Log the hex format of Protobuf data
//
//    // Construct the HTTP request to forward to Lambda
//    val httpRequest = HttpRequest(
//      method = HttpMethods.POST,
//      uri = apiUrl,
//      entity = HttpEntity(applicationGrpcProto, requestBytes)
//    ).withHeaders(
//      RawHeader("Content-Type", "application/grpc+proto"),
//      RawHeader("grpc-encoding", "identity"),
//      RawHeader("grpc-accept-encoding", "identity")
//    )
//
//    // Log the HTTP request details (URI, headers)
//    println(s"Sending HTTP request to $apiUrl")
//    println(s"Method: ${httpRequest.method}")
//    println(s"URI: ${httpRequest.uri}")
//    println(s"Headers: ${httpRequest.headers}")
//    println(s"Entity: ${httpRequest.entity}")
//
//    // Send the request to AWS API Gateway
//    Http().singleRequest(httpRequest)
//  }
//
//  // Main method to start the Akka HTTP server
//  def main(args: Array[String]): Unit = {
//    // Bind the Akka HTTP server to listen on port 8081
//    val bindingFuture = Http().newServerAt("0.0.0.0", 8081).bind(route)
//
//    // Inform that the server is running
//    bindingFuture.map { binding =>
//      println(s"Server is running at ${binding.localAddress}")
//    }.recover {
//      case ex: Exception => println(s"Failed to bind HTTP server: ${ex.getMessage}")
//    }
//
//    // Wait for user input (Enter key) to stop the server
//    println("Press Enter to stop the server...")
//    scala.io.StdIn.readLine()  // Block and wait for Enter key
//
//    // Gracefully stop the server
//    bindingFuture.flatMap(_.unbind()).onComplete { _ =>
//      println("Server stopped.")
//      system.terminate()
//    }
//  }
//}

// deser
//
//import akka.actor.ActorSystem
//import akka.http.scaladsl.Http
//import akka.http.scaladsl.server.Directives._
//import akka.stream.ActorMaterializer
//import akka.http.scaladsl.model._
//import akka.http.scaladsl.model.headers.RawHeader
//import akka.http.scaladsl.model.HttpCharsets
//import akka.util.ByteString
//import scala.concurrent.Future
//import lambda.lambda.{GenerateRequest, GenerateResponse}
//
//object LambdaForwardingService {
//
//  implicit val system: ActorSystem = ActorSystem("lambda-forwarding-system")
//  implicit val materializer: ActorMaterializer = ActorMaterializer()
//  implicit val executionContext = system.dispatcher
//
//  // Define the custom media type for gRPC + Proto
//  val applicationGrpcProto: MediaType.WithFixedCharset =
//    MediaType.customWithFixedCharset("application", "grpc+proto", HttpCharsets.`UTF-8`)
//
//  // Endpoint to handle user input and forward to AWS API Gateway
//  val route =
//    path("invokeLambda") {
//      post {
//        entity(as[Array[Byte]]) { requestBytes =>
//          // Log the incoming Protobuf request
//          println(s"Received request (size: ${requestBytes.length} bytes):")
//          println(requestBytes.map("%02X".format(_)).mkString(" "))  // Log the hex format of Protobuf data
//
////          // Deserialize the incoming Protobuf request
//           val generateRequest = GenerateRequest.parseFrom(requestBytes)
////
////          // Log the deserialized request (optional for checking values)
////          println("Deserialized GenerateRequest:")
////          println(generateRequest)
//
//          // Forward the request to Lambda via the API Gateway
//          val responseFuture: Future[HttpResponse] = forwardToLambda(generateRequest)
//
//          // Handle the response from Lambda
//          onComplete(responseFuture) {
//            case scala.util.Success(response) =>
//              // Combine the response body into a single ByteString
//              val combinedBytesFuture = response.entity.dataBytes.runFold(ByteString.empty)(_ ++ _)
//
//              // Use map to ensure the response is processed and returned as an HTTP entity
//              onComplete(combinedBytesFuture) {
//                case scala.util.Success(combinedBytes) =>
//                  complete(HttpEntity(applicationGrpcProto, combinedBytes))
//
//                case scala.util.Failure(exception) =>
//                  complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Error: ${exception.getMessage}"))
//              }
//
//            case scala.util.Failure(exception) =>
//              complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Error: ${exception.getMessage}"))
//          }
//        }
//      }
//    }
//
//  // Function to forward the request to AWS Lambda through the API Gateway
//  def forwardToLambda(request: GenerateRequest): Future[HttpResponse] = {
//    val apiUrl = "https://0nuai7kiha.execute-api.ap-southeast-2.amazonaws.com/invoke/LLM_Lambda"  // Replace with your actual API Gateway URL
//
//    // Serialize the request to a byte array
//    val requestBytes = request.toByteArray
//
//    // Log the Protobuf data that is being sent to the API Gateway
//    println(s"Forwarding request to API Gateway (size: ${requestBytes.length} bytes):")
//    println(requestBytes.map("%02X".format(_)).mkString(" "))  // Log the hex format of Protobuf data
//
//    // Construct the HTTP request to forward to Lambda
//    val httpRequest = HttpRequest(
//      method = HttpMethods.POST,
//      uri = apiUrl,
//      entity = HttpEntity(applicationGrpcProto, requestBytes)
//    ).withHeaders(
//      RawHeader("Content-Type", "application/grpc+proto"),
//      RawHeader("grpc-encoding", "identity"),
//      RawHeader("grpc-accept-encoding", "identity")
//    )
//
//    // Log the HTTP request details (URI, headers)
//    println(s"Sending HTTP request to $apiUrl")
//    println(s"Method: ${httpRequest.method}")
//    println(s"URI: ${httpRequest.uri}")
//    println(s"Headers: ${httpRequest.headers}")
//    println(s"Entity: ${httpRequest.entity}")
//
//    // Send the request to AWS API Gateway
//    Http().singleRequest(httpRequest)
//  }
//
//  // Main method to start the Akka HTTP server
//  def main(args: Array[String]): Unit = {
//    // Bind the Akka HTTP server to listen on port 8081
//    val bindingFuture = Http().newServerAt("0.0.0.0", 8081).bind(route)
//
//    // Inform that the server is running
//    bindingFuture.map { binding =>
//      println(s"Server is running at ${binding.localAddress}")
//    }.recover {
//      case ex: Exception => println(s"Failed to bind HTTP server: ${ex.getMessage}")
//    }
//
//    // Wait for user input (Enter key) to stop the server
//    println("Press Enter to stop the server...")
//    scala.io.StdIn.readLine()  // Block and wait for Enter key
//
//    // Gracefully stop the server
//    bindingFuture.flatMap(_.unbind()).onComplete { _ =>
//      println("Server stopped.")
//      system.terminate()
//    }
//  }
//}
//

//check 1
//import akka.actor.ActorSystem
//import akka.http.scaladsl.Http
//import akka.http.scaladsl.server.Directives._
//import akka.stream.ActorMaterializer
//import akka.http.scaladsl.model._
//import akka.http.scaladsl.model.headers.RawHeader
//import akka.http.scaladsl.model.HttpCharsets
//import akka.util.ByteString
//import scala.concurrent.Future
//import lambda.lambda.{GenerateRequest, GenerateResponse}
//
//object LambdaForwardingService {
//
//  implicit val system: ActorSystem = ActorSystem("lambda-forwarding-system")
//  implicit val materializer: ActorMaterializer = ActorMaterializer()
//  implicit val executionContext = system.dispatcher
//
//  // Define the custom media type for gRPC + Proto
//  val applicationGrpcProto: MediaType.WithFixedCharset =
//    MediaType.customWithFixedCharset("application", "grpc+proto", HttpCharsets.`UTF-8`)
//
//  // Endpoint to handle user input and forward to AWS API Gateway
//  val route =
//    path("invokeLambda") {
//      post {
//        entity(as[Array[Byte]]) { requestBytes =>
//          // Log the incoming Protobuf request
//          println(s"Received request (size: ${requestBytes.length} bytes):")
//          println(requestBytes.map("%02X".format(_)).mkString(" "))  // Log the hex format of Protobuf data
//
//          // Deserialize the incoming Protobuf request
//          val generateRequest = GenerateRequest.parseFrom(requestBytes)
//
//          // Log the deserialized request (optional for checking values)
//          println("Deserialized GenerateRequest:")
//          println(generateRequest)
//
//          // Forward the request to Lambda via the API Gateway
//          val responseFuture: Future[HttpResponse] = forwardToLambda(generateRequest)
//
//          // Handle the response from Lambda
//          onComplete(responseFuture) {
//            case scala.util.Success(response) =>
//              // Combine the response body into a single ByteString
//              val combinedBytesFuture = response.entity.dataBytes.runFold(ByteString.empty)(_ ++ _)
//
//              // Use map to ensure the response is processed and returned as an HTTP entity
//              onComplete(combinedBytesFuture) {
//                case scala.util.Success(combinedBytes) =>
//                  complete(HttpEntity(applicationGrpcProto, combinedBytes))
//
//                case scala.util.Failure(exception) =>
//                  complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Error: ${exception.getMessage}"))
//              }
//
//            case scala.util.Failure(exception) =>
//              complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Error: ${exception.getMessage}"))
//          }
//        }
//      }
//    }
//
//  // Function to forward the request to AWS Lambda through the API Gateway
//  def forwardToLambda(request: GenerateRequest): Future[HttpResponse] = {
//    val apiUrl = "https://0nuai7kiha.execute-api.ap-southeast-2.amazonaws.com/invoke/LLM_Lambda"  // Replace with your actual API Gateway URL
//
//    // Serialize the request to a byte array
//    val requestBytes = request.toByteArray
//
//    // Log the Protobuf data that is being sent to the API Gateway
//    println(s"Forwarding request to API Gateway (size: ${requestBytes.length} bytes):")
//    println(requestBytes.map("%02X".format(_)).mkString(" "))  // Log the hex format of Protobuf data
//
//    // Construct the HTTP request to forward to Lambda
//    val httpRequest = HttpRequest(
//      method = HttpMethods.POST,
//      uri = apiUrl,
//      entity = HttpEntity(applicationGrpcProto, requestBytes)
//    ).withHeaders(
//      RawHeader("Content-Type", "application/grpc+proto"),
//      RawHeader("grpc-encoding", "identity"),
//      RawHeader("grpc-accept-encoding", "identity")
//    )
//
//    // Log the HTTP request details (URI, headers)
//    println(s"Sending HTTP request to $apiUrl")
//    println(s"Method: ${httpRequest.method}")
//    println(s"URI: ${httpRequest.uri}")
//    println(s"Headers: ${httpRequest.headers}")
//    println(s"Entity: ${httpRequest.entity}")
//
//    // Print the request content that is being sent (Protobuf in hex format)
//    println("Request Body (Hex Format):")
//    println(requestBytes.map("%02X".format(_)).mkString(" "))
//
//    // Send the request to AWS API Gateway
//    Http().singleRequest(httpRequest)
//  }
//
//  // Main method to start the Akka HTTP server
//  def main(args: Array[String]): Unit = {
//    // Bind the Akka HTTP server to listen on port 8081
//    val bindingFuture = Http().newServerAt("0.0.0.0", 8081).bind(route)
//
//    // Inform that the server is running
//    bindingFuture.map { binding =>
//      println(s"Server is running at ${binding.localAddress}")
//    }.recover {
//      case ex: Exception => println(s"Failed to bind HTTP server: ${ex.getMessage}")
//    }
//
//    // Wait for user input (Enter key) to stop the server
//    println("Press Enter to stop the server...")
//    scala.io.StdIn.readLine()  // Block and wait for Enter key
//
//    // Gracefully stop the server
//    bindingFuture.flatMap(_.unbind()).onComplete { _ =>
//      println("Server stopped.")
//      system.terminate()
//    }
//  }
//}


//check 2
//import akka.actor.ActorSystem
//import akka.http.scaladsl.Http
//import akka.http.scaladsl.server.Directives._
//import akka.stream.ActorMaterializer
//import akka.http.scaladsl.model._
//import akka.http.scaladsl.model.headers.{RawHeader, `Content-Type`}
//import akka.http.scaladsl.model.HttpCharsets
//import akka.http.scaladsl.model.MediaType.WithFixedCharset
//import akka.util.ByteString
//
//import scala.concurrent.Future
//import lambda.lambda.{GenerateRequest, GenerateResponse}
//
//object LambdaForwardingService {
//
//  implicit val system: ActorSystem = ActorSystem("lambda-forwarding-system")
//  implicit val materializer: ActorMaterializer = ActorMaterializer()
//  implicit val executionContext = system.dispatcher
//
//  // Define the custom media type for gRPC + Proto
//
//  val `applicationGrpcProto`: WithFixedCharset = MediaType.customWithFixedCharset(
//    "application",
//    "grpc+proto",
//    HttpCharsets.`UTF-8`
//  )
//
//  // Endpoint to handle user input and forward to AWS API Gateway
//  val route =
//    path("invokeLambda") {
//      post {
//        entity(as[Array[Byte]]) { requestBytes =>
//          // Log the incoming Protobuf request
//          println(s"Received request (size: ${requestBytes.length} bytes):")
//          println(requestBytes.map("%02X".format(_)).mkString(" "))  // Log the hex format of Protobuf data
//
//          // Deserialize the incoming Protobuf request
//          val generateRequest = GenerateRequest.parseFrom(requestBytes)
//          println()
//
//          // Log the deserialized request (optional for checking values)
//          println("Deserialized GenerateRequest:")
//          println(generateRequest)
//
//          // Forward the request to Lambda via the API Gateway
//          val responseFuture: Future[HttpResponse] = forwardToLambda(generateRequest)
//
//          // Handle the response from Lambda
//          onComplete(responseFuture) {
//            case scala.util.Success(response) =>
//              // Log the status code from Lambda's response
//              println(s"Received response from Lambda: ${response.status}")
//
//              // Combine the response body into a single ByteString
//              val combinedBytesFuture = response.entity.dataBytes.runFold(ByteString.empty)(_ ++ _)
//
//              // Use map to ensure the response is processed and returned as an HTTP entity
//              onComplete(combinedBytesFuture) {
//                case scala.util.Success(combinedBytes) =>
//                  complete(HttpEntity(applicationGrpcProto, combinedBytes))
//
//                case scala.util.Failure(exception) =>
//                  complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Error: ${exception.getMessage}"))
//              }
//
//            case scala.util.Failure(exception) =>
//              complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Error: ${exception.getMessage}"))
//          }
//        }
//      }
//    }
//
//  // Function to forward the request to AWS Lambda through the API Gateway
//  def forwardToLambda(request: GenerateRequest): Future[HttpResponse] = {
//    val apiUrl = "https://5am3tl8xa0.execute-api.ap-southeast-2.amazonaws.com/invoke/LambdaFunction"  // Replace with your actual API Gateway URL
//
//    println("De")
//    println(request)
//    // Serialize the request to a byte array
//    val requestBytes = request.toByteArray
//
//    // Log the Protobuf data that is being sent to the API Gateway
//    println(s"Forwarding request to API Gateway (size: ${requestBytes.length} bytes):")
//    println(requestBytes.map("%02X".format(_)).mkString(" "))  // Log the hex format of Protobuf data
//
//    // Construct the HTTP request to forward to Lambda
//    val httpRequest = HttpRequest(
//      method = HttpMethods.POST,
//      uri = apiUrl,
//      entity = HttpEntity(applicationGrpcProto, requestBytes)
//    ).withHeaders(
////      headers.`Content-Type`(`applicationGrpcProto`),
//      headers.RawHeader("grpc-encoding", "identity"),
//      headers.RawHeader("grpc-accept-encoding", "identity")
//    )
//
//    // Log the HTTP request details (URI, headers)
//    println(s"Sending HTTP request to $apiUrl")
//    println(s"Method: ${httpRequest.method}")
//    println(s"URI: ${httpRequest.uri}")
//    println(s"Headers: ${httpRequest.headers}")
//    println(s"Entity: ${httpRequest.entity}")
//
//    // Print the request content that is being sent (Protobuf in hex format)
//    println("Request Body (Hex Format):")
//    println(requestBytes)
//
//    // Send the request to AWS API Gateway
//    Http().singleRequest(httpRequest)
//  }
//
//  // Main method to start the Akka HTTP server
//  def main(args: Array[String]): Unit = {
//    // Bind the Akka HTTP server to listen on port 8081
//    val bindingFuture = Http().newServerAt("0.0.0.0", 8081).bind(route)
//
//    // Inform that the server is running
//    bindingFuture.map { binding =>
//      println(s"Server is running at ${binding.localAddress}")
//    }.recover {
//      case ex: Exception => println(s"Failed to bind HTTP server: ${ex.getMessage}")
//    }
//
//    // Wait for user input (Enter key) to stop the server
//    println("Press Enter to stop the server...")
//    scala.io.StdIn.readLine()  // Block and wait for Enter key
//
//    // Gracefully stop the server
//    bindingFuture.flatMap(_.unbind()).onComplete { _ =>
//      println("Server stopped.")
//      system.terminate()
//    }
//  }
//}



import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{RawHeader, `Content-Type`}
import akka.http.scaladsl.model.HttpCharsets
import akka.http.scaladsl.model.MediaType.WithFixedCharset
import akka.util.ByteString
import org.slf4j.LoggerFactory
import scala.concurrent.Future
import lambda.lambda.{GenerateRequest, GenerateResponse}

object LambdaForwardingService {

  // Initialize SLF4J logger
  private val logger = LoggerFactory.getLogger(getClass)

  implicit val system: ActorSystem = ActorSystem("lambda-forwarding-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  // Define the custom media type for gRPC + Proto
  val `applicationGrpcProto`: WithFixedCharset = MediaType.customWithFixedCharset(
    "application",
    "grpc+proto",
    HttpCharsets.`UTF-8`
  )

  // Endpoint to handle user input and forward to AWS API Gateway
  val route =
    path("invokeLambda") {
      post {
        entity(as[Array[Byte]]) { requestBytes =>
          // Log the incoming Protobuf request
          logger.info(s"Received request (size: ${requestBytes.length} bytes):")
          logger.debug(requestBytes.map("%02X".format(_)).mkString(" "))  // Log the hex format of Protobuf data

          // Deserialize the incoming Protobuf request
          val generateRequest = GenerateRequest.parseFrom(requestBytes)

          // Log the deserialized request
          logger.debug(s"Deserialized GenerateRequest: $generateRequest")

          // Forward the request to Lambda via the API Gateway
          val responseFuture: Future[HttpResponse] = forwardToLambda(generateRequest)

          // Handle the response from Lambda
          onComplete(responseFuture) {
            case scala.util.Success(response) =>
              // Log the status code from Lambda's response
              logger.info(s"Received response from Lambda: ${response.status}")

              // Combine the response body into a single ByteString
              val combinedBytesFuture = response.entity.dataBytes.runFold(ByteString.empty)(_ ++ _)

              // Use map to ensure the response is processed and returned as an HTTP entity
              onComplete(combinedBytesFuture) {
                case scala.util.Success(combinedBytes) =>
                  complete(HttpEntity(applicationGrpcProto, combinedBytes))

                case scala.util.Failure(exception) =>
                  logger.error(s"Error processing response: ${exception.getMessage}")
                  complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Error: ${exception.getMessage}"))
              }

            case scala.util.Failure(exception) =>
              logger.error(s"Error forwarding request to Lambda: ${exception.getMessage}")
              complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Error: ${exception.getMessage}"))
          }
        }
      }
    }

  // Function to forward the request to AWS Lambda through the API Gateway
  def forwardToLambda(request: GenerateRequest): Future[HttpResponse] = {
    val apiUrl = "https://5am3tl8xa0.execute-api.ap-southeast-2.amazonaws.com/invoke/LambdaFunction"  // Replace with your actual API Gateway URL

    logger.debug(s"Forwarding request to API Gateway: $request")

    // Serialize the request to a byte array
    val requestBytes = request.toByteArray

    // Log the Protobuf data that is being sent to the API Gateway
    logger.debug(s"Forwarding request to API Gateway (size: ${requestBytes.length} bytes):")
    logger.debug(requestBytes.map("%02X".format(_)).mkString(" "))  // Log the hex format of Protobuf data

    // Construct the HTTP request to forward to Lambda
    val httpRequest = HttpRequest(
      method = HttpMethods.POST,
      uri = apiUrl,
      entity = HttpEntity(applicationGrpcProto, requestBytes)
    ).withHeaders(
      headers.RawHeader("grpc-encoding", "identity"),
      headers.RawHeader("grpc-accept-encoding", "identity")
    )

    // Log the HTTP request details (URI, headers)
    logger.debug(s"Sending HTTP request to $apiUrl")
    logger.debug(s"Method: ${httpRequest.method}")
    logger.debug(s"URI: ${httpRequest.uri}")
    logger.debug(s"Headers: ${httpRequest.headers}")
    logger.debug(s"Entity: ${httpRequest.entity}")

    // Print the request content that is being sent (Protobuf in hex format)
    logger.debug("Request Body (Hex Format):")
    logger.debug(requestBytes.map("%02X".format(_)).mkString(" "))

    // Send the request to AWS API Gateway
    Http().singleRequest(httpRequest)
  }

  // Main method to start the Akka HTTP server
  def main(args: Array[String]): Unit = {
    // Bind the Akka HTTP server to listen on port 8081
    val bindingFuture = Http().newServerAt("0.0.0.0", 8081).bind(route)

    // Inform that the server is running
    bindingFuture.map { binding =>
      logger.info(s"Server is running at ${binding.localAddress}")
    }.recover {
      case ex: Exception =>
        logger.error(s"Failed to bind HTTP server: ${ex.getMessage}")
    }

    // Wait for user input (Enter key) to stop the server
    logger.info("Press Enter to stop the server...")
    scala.io.StdIn.readLine()  // Block and wait for Enter key

    // Gracefully stop the server
    bindingFuture.flatMap(_.unbind()).onComplete { _ =>
      logger.info("Server stopped.")
      system.terminate()
    }
  }
}
