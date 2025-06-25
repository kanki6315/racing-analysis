FROM gradle:8.7.0-jdk21-alpine AS build
WORKDIR /app
COPY . .
ARG SPRING_DATASOURCE_URL
ARG SPRING_DATASOURCE_USERNAME
ARG SPRING_DATASOURCE_PASSWORD
ENV SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL
ENV SPRING_DATASOURCE_USERNAME=$SPRING_DATASOURCE_USERNAME
ENV SPRING_DATASOURCE_PASSWORD=$SPRING_DATASOURCE_PASSWORD
RUN ./gradlew flywayMigrate
RUN ./gradlew bootJar

# ---- Run Stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy the built JAR (any version) and rename to app.jar
COPY --from=build /app/build/libs/racing-stat-analysis-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]