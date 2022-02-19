FROM maven:3.8.4-jdk-11-slim AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

FROM openjdk:11-jre-slim
COPY --from=builder /app/target/graphical-csv-processing-0.0.1-SNAPSHOT.jar graphical-csv-processing.jar
ENV PORT 8080
EXPOSE 8080
CMD ["java","-jar","graphical-csv-processing.jar"]
