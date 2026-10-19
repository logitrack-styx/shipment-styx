
# Etapa 1: compilación de la aplicación.
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw && ./mvnw -q dependency:go-offline

COPY src/ src/

RUN ./mvnw -q clean package -DskipTests


# Etapa 2: ejecución de la aplicación.
FROM eclipse-temurin:25-jre

WORKDIR /app

RUN useradd --system --no-create-home appuser

COPY --from=build /app/target/*.jar app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
