# Use an official Java 8 runtime as a parent image
FROM openjdk:8-jre-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the compiled Spring Boot application JAR file into the container at the specified location
COPY target/alseyahat-0.0.1-SNAPSHOT.jar alseyahat-0.0.1-SNAPSHOT.jar

# Expose the port your Spring Boot application will listen on (adjust the port if necessary)
EXPOSE 8085

# Specify the command to run your Spring Boot application when the container starts
CMD ["java", "-jar", "alseyahat-0.0.1-SNAPSHOT.jar"]
