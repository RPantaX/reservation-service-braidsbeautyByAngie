# reservation-service/Dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY application/target/application-0.0.1-SNAPSHOT.jar reservation-service.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "reservation-service.jar"]