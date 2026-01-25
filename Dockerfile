# Use a slim OpenJDK base image for smaller image size
FROM openjdk:17-jdk-slim as builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle build files and source code
COPY build.gradle settings.gradle ./
# Copy the credentials.json to src/main/resources as expected by the app
# IMPORTANT: For production, manage credentials.json securely.
# Consider mounting it as a volume or using Docker/Kubernetes secrets
# instead of baking it into the image directly.
COPY src/main/resources/credentials.json src/main/resources/
COPY src ./src

# Build the Spring Boot application
RUN chmod +x gradlew
RUN ./gradlew bootJar

# Use a JRE base image for the final, smaller runtime image
FROM openjdk:17-jre-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Define the entrypoint to run the Spring Boot application
# This allows passing CLI arguments directly to the Java application.
ENTRYPOINT ["java", "-jar", "app.jar"]

# Example of how to build and run the Docker image:
#
# 1. Build the image (run this from your project root directory):
#    docker build -t issue-tracker-cli .
#
# 2. Run the image with a command (example: create a new issue):
#    docker run issue-tracker-cli create --description "New issue from Docker"
#
# 3. Run the image interactively (for the shell):
#    docker run -it issue-tracker-cli
#
#    Inside the container's shell, you can then type commands:
#    issue-tracker> create --description "Another issue from interactive shell"
#    issue-tracker> list --status OPEN
#
# IMPORTANT: Remember to replace 'credentials.json' and 'application.properties'
# contents with your actual Google Sheets ID and credentials if you're not
# mounting them as volumes. For production, consider using Docker secrets
# or Kubernetes secrets to manage sensitive information.
