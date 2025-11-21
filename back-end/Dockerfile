# ---- Build stage ----
FROM gradle:8.10-jdk17 AS build
WORKDIR /app

# Copy everything needed for the build
COPY . .

# Build the Spring Boot jar
RUN gradle clean bootJar --no-daemon

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built jar from the Gradle image
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
