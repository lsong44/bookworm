# Use the official OpenJDK 17 image as the base image
FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu

# Set the working directory
WORKDIR /app

ENV TEST_HOST=http://64.236.84.242

# Copy the Maven project file from the mainProj directory
COPY pom.xml .

# Copy the source code
COPY src ./src

# Install Maven
RUN apt-get update && \
    apt-get install -y maven


# Set the entry point to run the tests
ENTRYPOINT [ "sh", "-c", "mvn clean test" ]