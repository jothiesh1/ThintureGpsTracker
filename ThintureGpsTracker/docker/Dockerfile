# Use the official OpenJDK 17 image as the base image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container
# Adjust the path based on your project structure
COPY ../target/ThintureGpsTracker-0.0.1-SNAPSHOT.jar app.jar

# Add a health check for the application (optional)
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://3.109.116.92:8181/actuator/health || exit 1

# Expose the application port
EXPOSE 8182

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
