FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn --batch-mode --no-transfer-progress dependency:go-offline
COPY src src
RUN mvn --batch-mode --no-transfer-progress -DskipTests clean package

FROM eclipse-temurin:21-jre-jammy
RUN groupadd --system --gid 10001 vichayal && useradd --system --uid 10001 --gid vichayal --home /app vichayal
WORKDIR /app
COPY --from=build --chown=vichayal:vichayal /workspace/target/gestion-riego-*.jar app.jar
USER 10001:10001
EXPOSE 8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar app.jar"]
