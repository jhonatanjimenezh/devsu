FROM gradle:7.6.0-jdk17 AS build

WORKDIR /app

COPY build.gradle settings.gradle /app/
COPY src /app/src

RUN gradle build --no-daemon -x test

FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=build /app/build/libs/ws-account-0.0.1-SNAPSHOT.jar /app/ws-account.jar

COPY .env /app/.env

EXPOSE 8081

CMD ["sh", "-c", "source /app/.env && java -jar /app/ws-account.jar"]