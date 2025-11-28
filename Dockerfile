# ------------------------------------------------------------------------------------------------
# ETAPA 1: BUILD (Compilação)
# Usa uma imagem Java que já tem o Maven (Mvn) instalado.
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia os arquivos de build (pom.xml) e o wrapper do Maven (mvnw)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn/

# Baixa as dependências.
# Usa o mvnw (Maven Wrapper) do seu projeto.
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Copia todo o código-fonte
COPY src src

# Executa o build final, gerando o arquivo JAR
RUN ./mvnw package -DskipTests

# ------------------------------------------------------------------------------------------------
# ETAPA 2: RUNTIME (Execução)
# Usa uma imagem Java menor e mais segura (apenas para rodar a aplicação, sem o Maven).
FROM eclipse-temurin:21-jre-alpine

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo JAR gerado na fase "build" para a fase "runtime"
# Note que o nome do seu JAR é googleSheets-0.0.1-SNAPSHOT.jar
COPY --from=build /app/target/googleSheets-0.0.1-SNAPSHOT.jar app.jar

# Define a porta que a aplicação Spring Boot usará
EXPOSE 8080

# Comando para iniciar o servidor Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]