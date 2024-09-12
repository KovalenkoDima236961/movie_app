# Backend Dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the Spring Boot application JAR
COPY target/movie-app-0.0.1-SNAPSHOT.jar /app/movie-app.jar

# Expose the port Spring Boot runs on
EXPOSE 8080

# Run the Spring Boot application
CMD ["java", "-jar", "movie-app.jar"]
