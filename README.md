* * * * *

**CS441_GargiSathe_HW3**\
**Gargi Sathe**\
**UIN: 674491700**\
**NetID: <gsath4@uic.edu>**

Repo for the homework-3 for CS441-Fall2024

* * * * *

AWS EMR Deployment video link: https://youtu.be/cLWhmFdm3WI

* * * * *

Docker Deployment video link:

* * * * *

Running the project
-------------------

1.  Download this repo from git.
2.  You would have three projects cloned or copied to your location.
3.  From the terminal, run `sbt clean compile run` for both EC2_Server and Local_Conversation_Agent.
4.  From the terminal, run `sbt clean compile assembly` for Lambda_.
5.  To test, run `sbt test`.
6.  To create a .jar file, run the command `sbt clean compile assembly`. The resulting jar will be placed in `'ProjectName'/target/'scala-version'/_`.
7.  If you want to run it locally using Spark, you can run the below command:

**For Generating Conversation**

```
curl -X POST http://localhost:8080/api/conversation\
-H "Content-Type: application/json"\
-d '{
    "prompt": "prompt?",
}'

```

**For generating response from Ollama**

```
curl -X POST http://localhost:8081/api/ollama\
-H "Content-Type: application/json"\
-d '{
    "prompt": "prompt?",
}'

```

**For generating response from LLMServer**

```
curl -X POST http://localhost:8081/api/llmserver\
-H "Content-Type: application/json"\
-d '{
    "prompt": "prompt",
}'

```

* * * * *

Project Requirements
====================

This homework requires using Akka Http to create a server and send Request via REST API using grpc+protobuf serialization to AWS Lambda function.

### Requirements:

1.  **Create a Conversational Agent to Generate Conversation between locally hosted Ollama model and EC2 deployed LLMserver.**
2.  **Create an AKKAHTTP server in the EC2 instance to send grpc+Protobuf requests to the Lambda Function via API Gateway.**

### Additional requirements:

1.  Implement 5 or more ScalaTest cases.
2.  Logging must be used across all programs.
3.  Input and output paths must be configurable.
4.  The project must compile using sbt.
5.  The project should be deployable on AWS EMR.

* * * * *

Technical Design
----------------

This section describes the core components of the project, along with line-by-line explanations found in the source code.

Flow of the Conversation
------------------------

User Input via curl/Postman **>>** Conversational Agent **>>** Microservice hosted at EC2 Instance **>>** Protobuf request (Encoded in EC2 Microservice) **>>** LLM Model(Lambda) **>>** protobuf response(Encoded in EC2 Microservice) **>>** Microservice hosted at EC2 Instance **>>** Conversational Agent **>>** LLM Model(Llama3)

Technical Design Documentation
------------------------------

### 1\. Local_Conversational_Agent

The LocalConversationalAgent serves as the main entry point that coordinates the conversation and request flow.

* * * * *

### 2\. EC2_Server

The EC2_Server serves as the Server hosted on the EC2 Instance which includes conversion of JSON request to Protobuf and sends it to the Lambda function, which is used for serverless computing. The response is generated back.

* * * * *

### 3\. Lambda Function

Used to invoke the LLM

### 4\. API Gateway

Used to send the data through and through to the lambda in grpc+proto format

### 5\. Data Flow
![image](https://github.com/user-attachments/assets/7beb7d33-61c3-4c77-82ff-6f76052082f4)


### Test Cases:

![Test_cases_CS441](https://github.com/user-attachments/assets/6a53814d-6862-495d-b526-2ff1a524c4ab)

### Project Dependencies:
- Java 8
- Scala 2.12.13
- AWS account

