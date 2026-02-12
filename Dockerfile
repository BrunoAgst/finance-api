FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests=true -Dmaven.test.skip=true

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

RUN apt-get update && apt-get install -y curl bash gnupg2 && \
    curl -1sLf 'https://artifacts-cli.infisical.com/setup.deb.sh' | bash && \
    apt-get update && apt-get install -y infisical && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*.jar app.jar
RUN mkdir -p /app/logs /app/logs/archived
EXPOSE 8080