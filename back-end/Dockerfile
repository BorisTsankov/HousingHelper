# ---- Build stage ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY backend/gradlew backend/gradle ./
COPY backend/build.gradle backend/settings.gradle ./
COPY backend/src ./src
RUN chmod +x ./gradlew && ./gradlew clean bootJar --no-daemon

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copy final fat jar
COPY --from=build /app/build/libs/*.jar app.jar
# Expose your app port
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
