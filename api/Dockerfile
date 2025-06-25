FROM gradle:8.7.0-jdk21-alpine AS build
WORKDIR /app
COPY . .
RUN ./gradlew bootJar -Dspring.profiles.active=build --no-daemon

# ---- Run Stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy the built JAR (any version) and rename to app.jar
COPY --from=build /app/build/libs/racing-stat-analysis-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]