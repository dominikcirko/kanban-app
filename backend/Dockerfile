FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
RUN adduser -D appuser
USER appuser
CMD ["java", "-jar", "app.jar"]