# Use the official OpenJDK 17 image as the base image
FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu

# Set the working directory
WORKDIR /app

# Copy the Maven project file from the mainProj directory
COPY mainProj/pom.xml ./mainProj/

# Copy the source code
COPY mainProj/src ./mainProj/src

# Install Maven
RUN apt-get update && \
    apt-get install -y maven

# Change to the mainProj directory
WORKDIR /app/mainProj

# Package the application
RUN mvn clean package

# Set the entry point to run the application
ENTRYPOINT [ "sh", "-c", "java -jar target/*.jar" ]