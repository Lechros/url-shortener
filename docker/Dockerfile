FROM openjdk:17-alpine AS builder

WORKDIR /app

COPY gradle gradle
COPY gradlew ./
RUN chmod +x ./gradlew

COPY settings.gradle.kts ./
COPY build.gradle.kts ./
RUN ./gradlew dependencies

COPY src src
RUN ./gradlew bootJar

FROM openjdk:17-alpine AS server

WORKDIR /app

COPY --from=builder /app/build/libs/*-SNAPSHOT.jar ./app.jar

EXPOSE 8089

ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "/app/app.jar"]
