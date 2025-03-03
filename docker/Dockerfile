#FROM ubuntu:latest
#LABEL authors="gargi"
#
#ENTRYPOINT ["top", "-b"]
#
## Use an official OpenJDK runtime as a parent image
#FROM openjdk:8-jdk-alpine
#
## Set environment variables for Scala and SBT
#ENV SCALA_VERSION=2.12.13
#ENV SBT_VERSION=1.5.5
#
## Install SBT
#RUN apk add --no-cache bash curl && \
#    curl -s https://get.sdkman.io | bash && \
#    source $HOME/.sdkman/bin/sdkman-init.sh && \
#    sdk install sbt $SBT_VERSION
#
## Set the working directory in the container
#WORKDIR /app
#
## Copy the SBT build files
#COPY build.sbt /app/
#COPY project /app/project
#
## Copy the source code into the container
#COPY src /app/src
#
## Fetch dependencies and compile the project
#RUN sbt clean compile
#
## Build the fat JAR file
#RUN sbt assembly
#
## Expose the application port (if running on port 8080)
#EXPOSE 8080
#
## Define the command to run your application
#CMD ["java", "-jar", "target/scala-2.12/your-application.jar"]

# PROTOC UNSUCCESSFUL ATTEMPT 1
#
#FROM openjdk:8-jdk-alpine
#
## Install curl and protoc (you may need to adjust the version based on your requirements)
#RUN apk add --no-cache curl bash \
#    && curl -Lo protoc.zip https://github.com/protocolbuffers/protobuf/releases/download/v3.19.2/protoc-3.19.2-linux-x86_64.zip \
#    && unzip protoc.zip -d /usr/local bin/protoc \
#    && chmod +x /usr/local/bin/protoc \
#    && rm protoc.zip
#
## Use an openjdk base image
#FROM openjdk:8-jdk-alpine
#
## Install necessary packages and SDKMAN for sbt and Scala installation
#RUN apk update && apk add --no-cache bash curl zip unzip && \
#    curl -s https://get.sdkman.io | bash && \
#    bash -c "source /root/.sdkman/bin/sdkman-init.sh && sdk install sbt && sdk install scala 2.12.20"
#
## Set environment variables for SDKMAN
#ENV SDKMAN_DIR=/root/.sdkman
#ENV PATH=${SDKMAN_DIR}/candidates/sbt/current/bin:${SDKMAN_DIR}/candidates/scala/current/bin:${PATH}
#
## Set working directory
#WORKDIR /app
#
## Copy the project files into the container
#COPY . ./
#
## Expose the port that the server will run on
#EXPOSE 8080
#
## Ensure the SDKMAN environment is sourced and then run sbt
#CMD ["bash", "-c", "source /root/.sdkman/bin/sdkman-init.sh && sbt run"]

# Stage 1: Build the application
FROM openjdk:8-jdk-alpine AS builder

# Set environment variables for the installation paths and versions
ENV PB_REL="https://github.com/protocolbuffers/protobuf/releases"
ENV PROTOC_VERSION="v3.19.2"
ENV SCALA_VERSION="2.12.13"
ENV SBT_VERSION="1.10.5"

WORKDIR /app

# Install necessary dependencies
RUN apk update && apk add --no-cache \
    curl \
    unzip \
    bash \
    git \
    && mkdir -p $HOME/.local/bin

# Download and install protoc
RUN curl -LO $PB_REL/download/$PROTOC_VERSION/protoc-3.19.2-linux-x86_64.zip \
    && unzip protoc-3.19.2-linux-x86_64.zip -d $HOME/.local \
    && rm protoc-3.19.2-linux-x86_64.zip \
    && export PATH="$PATH:$HOME/.local/bin"

# Ensure that protoc is available
RUN echo 'export PATH="$PATH:$HOME/.local/bin"' >> ~/.bashrc

# Install sbt (Scala build tool)
RUN curl -Ls https://github.com/sbt/sbt/releases/download/v$SBT_VERSION/sbt-$SBT_VERSION.tgz | tar xz -C /usr/local/ \
    && ln -s /usr/local/sbt/bin/sbt /usr/local/bin/sbt

# Copy project configuration files first to cache dependencies
COPY project ./project
COPY build.sbt ./

# Cache SBT dependencies
RUN sbt update

# Copy the source code
COPY src ./src

# Build the application
RUN sbt clean compile assembly

# Stage 2: Run the application
FROM openjdk:8-jre-alpine

WORKDIR /app

# Copy the assembled jar from the builder stage
COPY --from=builder /app/target/scala-$SCALA_VERSION/assembly.jar ./app.jar

# Expose the application port
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
