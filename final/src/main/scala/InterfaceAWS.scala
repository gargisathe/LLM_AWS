//package LLM_Interaction.AWS
//
//import com.knuddels.jtokkit.Encodings
//import com.knuddels.jtokkit.api.EncodingType
//import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
//import org.deeplearning4j.models.word2vec.Word2Vec
//import org.deeplearning4j.util.ModelSerializer
//import org.nd4j.linalg.factory.Nd4j
//import org.nd4j.linalg.api.ndarray.INDArray
//import software.amazon.awssdk.services.s3.S3Client
//import software.amazon.awssdk.services.s3.model._
//import scala.collection.mutable.ListBuffer
//import java.nio.file.{Paths, Files}
//import scala.collection.JavaConverters._
//
//object InterfaceAWS {
//
//  // S3 Bucket and file paths
//  val s3BucketName = "llmb"
//  val word2VecModelS3Key = "word2vecModel.zip"
//  val neuralModelS3Key = "neuralmodel.zip"
//  val tokenIdToWordMapS3Key = "A_EM.txt"
//
//  // Local paths to store downloaded models
//  val tmpWord2VecModelPath = "/tmp/word2vecModel.zip"
//  val tmpNeuralModelPath = "/tmp/neuralmodel.zip"
//  val tmpTokenIdToWordMapPath = "/tmp/A_EM.txt"
//
//  // Initialize S3 client
//  val s3Client = S3Client.create()
//
//  // Download file from S3 to /tmp/
//  def downloadFileFromS3(s3Key: String, localPath: String): Unit = {
//    // Check if the file already exists and delete it
//    val filePath = Paths.get(localPath)
//    if (Files.exists(filePath)) {
//      Files.delete(filePath)
//      println(s"Deleted existing file at $localPath")
//    }
//
//    // Now download the file from S3
//    val getObjectRequest = GetObjectRequest.builder()
//      .bucket(s3BucketName)
//      .key(s3Key)
//      .build()
//
//    val response = s3Client.getObject(getObjectRequest, filePath)
//    println(s"Downloaded $s3Key to $localPath")
//  }
//
//  // Load token ID to word map from S3
//  def loadTokenIdToWordMap(): Map[String, String] = {
//    downloadFileFromS3(tokenIdToWordMapS3Key, tmpTokenIdToWordMapPath)
//    val source = scala.io.Source.fromFile(tmpTokenIdToWordMapPath)
//    val tokenIdToWord = source.getLines().map { line =>
//      val parts = line.split("\\s+")
//      parts(1) -> parts(0)  // map the second column (ID) to the first column (word)
//    }.toMap
//    source.close()
//    tokenIdToWord
//  }
//
//  // TokenizerMapper logic
//  def tokenize(input: String): Seq[(String, String)] = {
//    val registry = Encodings.newDefaultEncodingRegistry()
//    val encoding = registry.getEncoding(EncodingType.CL100K_BASE)
//
//    val cleanedLine = input.replaceAll("[^a-zA-Z0-9\\s]", "")
//    val words = cleanedLine.split("\\s+").filter(_.nonEmpty)
//
//    words.zipWithIndex.map { case (word, index) =>
//      val tokenIds = encoding.encode(word)
//      val tokenIdsString = tokenIds.toArray.mkString(" ")
//      (s"$index", s"$word $tokenIdsString")
//    }
//  }
//
//  // Generate embeddings using Word2Vec model from S3
//  def generateEmbeddings(tokensWithWords: Seq[(String, String)]): Map[String, String] = {
//    // Download Word2Vec model from S3
//    downloadFileFromS3(word2VecModelS3Key, tmpWord2VecModelPath)
//    val word2Vec = WordVectorSerializer.readWord2VecModel(tmpWord2VecModelPath)
//
//    tokensWithWords.map { case (_, value) =>
//      val parts = value.split("\\s+")
//      val word = parts(0)
//      val token = parts(1)
//
//      val embedding = if (word2Vec.hasWord(token)) {
//        word2Vec.getWordVector(token).mkString("[", ", ", "]")
//      } else {
//        val zeroVector = Array.fill(word2Vec.getLayerSize)(0.0)
//        println(s"Token not found: $word ($token). Using zero vector as fallback.")
//        zeroVector.mkString("[", ", ", "]")
//      }
//      word -> embedding
//    }.toMap
//  }
//
//  // Apply sliding window method to embeddings
//  def applySlidingWindow(embeddings: Map[String, String], windowSize: Int = 5): Seq[INDArray] = {
//    val tokenEmbeddings = embeddings.toSeq
//
//    val slidingWindows = tokenEmbeddings.sliding(windowSize).filter(_.length == windowSize).map { window =>
//      val contextEmbeddings = window.flatMap { case (_, embedding) =>
//        embedding.replace("[", "").replace("]", "").split(",").map(_.toDouble)
//      }
//      Nd4j.create(contextEmbeddings.toArray)
//    }.toSeq
//
//    slidingWindows
//  }
//
//  // Predict next words using neural model
//  def predictNextWords(contextVectors: Seq[INDArray], neuralModelPath: String, word2Vec: Word2Vec, tokenIdToWordMap: Map[String, String]): Seq[String] = {
//    // Download neural model from S3
//    downloadFileFromS3(neuralModelS3Key, tmpNeuralModelPath)
//    val model = ModelSerializer.restoreMultiLayerNetwork(tmpNeuralModelPath)
//
//    val predictedWords = ListBuffer[String]()
//
//    contextVectors.foreach { context =>
//      val input = context.reshape(1, context.length())
//      val predictedEmbedding = model.output(input, false)
//
//      val nearestTokenIds = word2Vec.wordsNearest(predictedEmbedding, 5).asScala
//      val nearestWords = nearestTokenIds.flatMap(tokenIdToWordMap.get)
//      predictedWords ++= nearestWords
//    }
//
//    predictedWords.toSeq
//  }
//}


package LLM_Interaction.AWS

import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.EncodingType
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.word2vec.Word2Vec
import org.deeplearning4j.util.ModelSerializer
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.api.ndarray.INDArray
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model._
import scala.collection.mutable.ListBuffer
import java.nio.file.{Paths, Files}
import scala.collection.JavaConverters._
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

object InterfaceAWS {

  // Initialize logger
  val logger = LoggerFactory.getLogger(getClass)

  // Load configuration from application.conf
  val config = ConfigFactory.load()

  // S3 Bucket and file paths from config
  val s3BucketName = config.getString("aws.s3.bucket")
  val word2VecModelS3Key = config.getString("aws.s3.word2VecModelKey")
  val neuralModelS3Key = config.getString("aws.s3.neuralModelKey")
  val tokenIdToWordMapS3Key = config.getString("aws.s3.tokenIdToWordMapKey")

  // Local paths to store downloaded models from config
  val tmpWord2VecModelPath = config.getString("local.paths.word2VecModelPath")
  val tmpNeuralModelPath = config.getString("local.paths.neuralModelPath")
  val tmpTokenIdToWordMapPath = config.getString("local.paths.tokenIdToWordMapPath")

  // Initialize S3 client
  val s3Client = S3Client.create()

  // Download file from S3 to /tmp/
  def downloadFileFromS3(s3Key: String, localPath: String): Unit = {
    // Check if the file already exists and delete it
    val filePath = Paths.get(localPath)
    if (Files.exists(filePath)) {
      Files.delete(filePath)
      logger.info(s"Deleted existing file at $localPath")
    }

    // Now download the file from S3
    try {
      val getObjectRequest = GetObjectRequest.builder()
        .bucket(s3BucketName)
        .key(s3Key)
        .build()

      val response = s3Client.getObject(getObjectRequest, filePath)
      logger.info(s"Downloaded $s3Key to $localPath")
    } catch {
      case e: Exception =>
        logger.error(s"Failed to download $s3Key to $localPath", e)
    }
  }

  // Load token ID to word map from S3
  def loadTokenIdToWordMap(): Map[String, String] = {
    try {
      downloadFileFromS3(tokenIdToWordMapS3Key, tmpTokenIdToWordMapPath)
      val source = scala.io.Source.fromFile(tmpTokenIdToWordMapPath)
      val tokenIdToWord = source.getLines().map { line =>
        val parts = line.split("\\s+")
        parts(1) -> parts(0)  // map the second column (ID) to the first column (word)
      }.toMap
      source.close()
      logger.info("Token ID to word map loaded successfully.")
      tokenIdToWord
    } catch {
      case e: Exception =>
        logger.error("Failed to load Token ID to word map.", e)
        Map.empty
    }
  }

  // TokenizerMapper logic
  def tokenize(input: String): Seq[(String, String)] = {
    val registry = Encodings.newDefaultEncodingRegistry()
    val encoding = registry.getEncoding(EncodingType.CL100K_BASE)

    val cleanedLine = input.replaceAll("[^a-zA-Z0-9\\s]", "")
    val words = cleanedLine.split("\\s+").filter(_.nonEmpty)

    val tokenized = words.zipWithIndex.map { case (word, index) =>
      val tokenIds = encoding.encode(word)
      val tokenIdsString = tokenIds.toArray.mkString(" ")
      (s"$index", s"$word $tokenIdsString")
    }

    logger.debug(s"Tokenized input: $input")
    tokenized
  }

  // Generate embeddings using Word2Vec model from S3
  def generateEmbeddings(tokensWithWords: Seq[(String, String)]): Map[String, String] = {
    try {
      // Download Word2Vec model from S3
      downloadFileFromS3(word2VecModelS3Key, tmpWord2VecModelPath)
      val word2Vec = WordVectorSerializer.readWord2VecModel(tmpWord2VecModelPath)

      val embeddings = tokensWithWords.map { case (_, value) =>
        val parts = value.split("\\s+")
        val word = parts(0)
        val token = parts(1)

        val embedding = if (word2Vec.hasWord(token)) {
          word2Vec.getWordVector(token).mkString("[", ", ", "]")
        } else {
          val zeroVector = Array.fill(word2Vec.getLayerSize)(0.0)
          logger.warn(s"Token not found: $word ($token). Using zero vector as fallback.")
          zeroVector.mkString("[", ", ", "]")
        }
        word -> embedding
      }

      logger.info(s"Generated embeddings for ${tokensWithWords.length} tokens.")
      embeddings.toMap
    } catch {
      case e: Exception =>
        logger.error("Failed to generate embeddings using Word2Vec model.", e)
        Map.empty
    }
  }

  // Apply sliding window method to embeddings
  def applySlidingWindow(embeddings: Map[String, String], windowSize: Int = 5): Seq[INDArray] = {
    val tokenEmbeddings = embeddings.toSeq

    val slidingWindows = tokenEmbeddings.sliding(windowSize).filter(_.length == windowSize).map { window =>
      val contextEmbeddings = window.flatMap { case (_, embedding) =>
        embedding.replace("[", "").replace("]", "").split(",").map(_.toDouble)
      }
      Nd4j.create(contextEmbeddings.toArray)
    }.toSeq

    logger.debug(s"Applied sliding window with size $windowSize.")
    slidingWindows
  }

  // Predict next words using neural model
  def predictNextWords(contextVectors: Seq[INDArray], neuralModelPath: String, word2Vec: Word2Vec, tokenIdToWordMap: Map[String, String]): Seq[String] = {
    try {
      // Download neural model from S3
      downloadFileFromS3(neuralModelS3Key, tmpNeuralModelPath)
      val model = ModelSerializer.restoreMultiLayerNetwork(tmpNeuralModelPath)

      val predictedWords = ListBuffer[String]()

      contextVectors.foreach { context =>
        val input = context.reshape(1, context.length())
        val predictedEmbedding = model.output(input, false)

        val nearestTokenIds = word2Vec.wordsNearest(predictedEmbedding, 5).asScala
        val nearestWords = nearestTokenIds.flatMap(tokenIdToWordMap.get)
        predictedWords ++= nearestWords
      }

      logger.info(s"Predicted next words using neural model.")
      predictedWords.toSeq
    } catch {
      case e: Exception =>
        logger.error("Failed to predict next words using neural model.", e)
        Seq.empty
    }
  }
}
