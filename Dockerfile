# Use an official Java 8 runtime as a parent image
# Stage 1: Build the Spring Boot application with Maven
FROM maven:3.8.4-openjdk-8 AS build
WORKDIR /app
COPY pom.xml .
COPY src src/
RUN mvn package -DskipTests

# Stage 2: Create a lightweight runtime image
FROM openjdk:8-jre-alpine
WORKDIR /app

# Copy the JAR file built in Stage 1 to the runtime image
COPY --from=build /app/target/alseyahat-0.0.1-SNAPSHOT.jar alseyahat-0.0.1-SNAPSHOT.jar

# Expose the port your Spring Boot application will listen on (adjust the port if necessary)
EXPOSE 8085

# Specify the command to run your Spring Boot application when the container starts
CMD ["java", "-jar", "alseyahat-0.0.1-SNAPSHOT.jar"]
