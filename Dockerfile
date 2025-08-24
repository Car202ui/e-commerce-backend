# syntax=docker/dockerfile:1

# --- Etapa de build (compila el .jar) ---
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# Descarga dependencias (offline) y evita correr tests
RUN mvn -q -B -DskipTests=true dependency:go-offline
COPY . .
RUN mvn -q -B -DskipTests=true package

# --- Etapa de runtime (imagen mínima para correr la app) ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# usuario no-root
RUN useradd -r -u 1001 appuser
# copia el jar generado (ajusta el nombre si tu target cambia)
COPY --from=build /app/target/e-commerce-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9091
ENV JAVA_OPTS="-Xms256m -Xmx512m"
USER appuser

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
