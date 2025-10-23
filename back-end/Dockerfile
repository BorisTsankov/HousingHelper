#Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY backend/gradlew backend/gradle ./
COPY backend/build.gradle backend/settings.gradle ./
COPY backend/src ./src
RUN chmod +x ./gradlew && ./gradlew clean bootJar --no-daemon

#Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
