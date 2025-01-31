# Use the official OpenJDK 17 image as the base image
FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu

ARG HOST=http://localhost:8080
ARG ACCESS_TOKEN=placeholder
# Set the working directory
WORKDIR /app

ENV TEST_HOST=${HOST}
ENV TEST_ACCESS_TOKEN=${ACCESS_TOKEN}

# Copy the Maven project file from the mainProj directory
COPY pom.xml .

# Copy the source code
COPY src ./src

# Install Maven
RUN apt-get update && \
    apt-get install -y maven