# Stage 1: Compilazione e Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia il file di configurazione delle dipendenze
COPY pom.xml .

# Scarica le dipendenze in anticipo (ottimizza la cache di Docker)
RUN mvn dependency:go-offline -B

# Copia i sorgenti del progetto
COPY src ./src

# Compila e crea il pacchetto jar (saltando i test per velocizzare, se preferisci)
RUN mvn package -DskipTests

# Stage 2: Immagine di Runtime leggera
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia il jar generato dallo stage di build
COPY --from=build /app/target/*.jar app.jar

# Espone la porta classica di Spring Boot
EXPOSE 8080

# Comando di avvio dell'applicazione
ENTRYPOINT ["java", "-jar", "app.jar"]