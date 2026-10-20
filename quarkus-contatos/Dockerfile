# syntax=docker/dockerfile:1

# ---------- Stage 1: build ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Camada de cache: resolve dependencias antes de copiar o codigo-fonte
COPY pom.xml ./
RUN mvn -B -ntp dependency:go-offline || true

COPY src ./src
# quarkus-maven-plugin:build gera o fast-jar em target/quarkus-app
#
# -Dquarkus.datasource.db-kind=postgresql: o Quarkus fixa o driver JDBC em
# BUILD TIME (Agroal registra apenas o driver do db-kind do build). Sem isso,
# a imagem sobe com db-kind=postgresql em runtime e falha com
# "Driver does not support the provided URL".
RUN mvn -B -ntp clean package -DskipTests -Dquarkus.datasource.db-kind=postgresql

# ---------- Stage 2: runtime ----------
FROM eclipse-temurin:17-jre-jammy AS runtime

# curl: usado pelo healthcheck do container
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

# usuario nao-root
RUN groupadd --system --gid 1001 app \
 && useradd --system --uid 1001 --gid app --home-dir /app --shell /usr/sbin/nologin app

# fast-jar: quarkus-run.jar precisa de lib/ e app/ no mesmo diretorio
WORKDIR /app
COPY --from=build --chown=app:app /build/target/quarkus-app/ ./
RUN chmod -R a+rX /app

USER app
EXPOSE 8081

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport -Dquarkus.http.host=0.0.0.0"

# exec: o JVM assume PID 1 e recebe os sinais do docker stop
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/quarkus-run.jar"]
