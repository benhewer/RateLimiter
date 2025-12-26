FROM eclipse-temurin:17-jre-alpine
COPY build/libs/rate-limiter-1.0.0.jar rate-limiter-1.0.0.jar
ENTRYPOINT ["java", "-jar", "rate-limiter-1.0.0.jar"]