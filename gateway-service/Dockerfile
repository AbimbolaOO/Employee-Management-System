# Stage 1: Build the application
FROM maven:3.8.6-eclipse-temurin-17-alpine AS build

# Set the working directory
WORKDIR /app

# Copy only the POM file first to leverage Docker cache
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Create a minimal runtime image
# Using Debian-based image instead of Alpine for native library compatibility
FROM openjdk:17-jdk-slim

# Add non-root user for security
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Set the working directory
WORKDIR /app

# Copy only the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Set ownership to non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose the port
EXPOSE 8080

# Configure JVM options for container environments with memory optimization
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", \
            "-Xms128m", "-Djava.security.egd=file:/dev/./urandom", \
            "-jar", "app.jar"]