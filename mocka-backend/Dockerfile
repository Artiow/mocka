# Build stage
FROM maven:3.6.3-amazoncorretto-11 AS build
LABEL stage=build
COPY src /app/src
COPY pom.xml /app
RUN mvn -f /app/pom.xml clean package

# Package stage
FROM amazoncorretto:11-alpine
COPY --from=build /app/target/mocka-backend.jar /opt/mocka-backend.jar
ENTRYPOINT ["java","-jar","/opt/mocka-backend.jar"]
EXPOSE 8080
