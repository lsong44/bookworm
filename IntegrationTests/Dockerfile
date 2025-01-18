# Use the official OpenJDK 17 image as the base image
FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu

ARG TEST_HOST=http://localhost:8080
# Set the working directory
WORKDIR /app

ENV TEST_HOST=${TEST_HOST}

# Copy the Maven project file from the mainProj directory
COPY pom.xml .

# Copy the source code
COPY src ./src

# Install Maven
RUN apt-get update && \
    apt-get install -y maven