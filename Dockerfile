FROM gradle:8-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon -x test

FROM eclipse-temurin:17-jre-alpine
COPY build/libs/rate-limiter-1.0.0.jar rate-limiter-1.0.0.jar
ENTRYPOINT ["java", "-jar", "rate-limiter-1.0.0.jar"]