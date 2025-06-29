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
# Download OpenTelemetry Java agent
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.51.0/opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar

ENV OTEL_RESOURCE_ATTRIBUTES="service.name=racing-analysis-api,service.namespace=com.arjunakankipati,deployment.environment=production" OTEL_EXPORTER_OTLP_PROTOCOL="http/protobuf"

EXPOSE 8080
ENTRYPOINT ["java", "-javaagent:/otel/opentelemetry-javaagent.jar", "-jar", "app.jar"]