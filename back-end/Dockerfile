# ---- build ----
FROM gradle:8.7-jdk21-alpine AS build
WORKDIR /app
COPY build.gradle settings.gradle gradlew* ./
COPY gradle ./gradle
RUN ./gradlew --no-daemon build -x test || return 0
COPY . .
RUN ./gradlew --no-daemon clean bootJar

# ---- run ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ENV JAVA_OPTS="-Xms256m -Xmx512m"
COPY --from=build /app/build/libs/*.jar /app/app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s CMD wget -qO- http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
