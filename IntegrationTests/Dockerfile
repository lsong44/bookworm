# Use the official OpenJDK 17 image as the base image
FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu

ARG HOST=http://localhost:8080
ARG ACCESS_TOKEN=placeholder
ARG OAUTH_CLIENT_ID
ARG OAUTH_CLIENT_SECRET
ARG OAUTH_SCOPE=openid
# Set the working directory
WORKDIR /app

ENV TEST_HOST=${HOST}
ENV OAUTH_ACCESS_TOKEN=${ACCESS_TOKEN}
ENV OAUTH_CLIENT_ID=${OAUTH_CLIENT_ID}
ENV OAUTH_CLIENT_SECRET=${OAUTH_CLIENT_SECRET}
ENV OAUTH_SCOPE=${OAUTH_SCOPE}

# Copy the Maven project file from the mainProj directory
COPY pom.xml .

# Copy the source code
COPY src ./src

# Install Maven
RUN apt-get update && \
    apt-get install -y maven