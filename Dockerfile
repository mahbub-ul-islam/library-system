# build stage
FROM maven:3.8.5-openjdk-17 AS MAVEN_BUILD

WORKDIR /app

# copy the pom and src code to the container
COPY pom.xml .
COPY src src

# build artifact with a fix name
RUN mvn clean install

# second stage of our build
FROM openjdk:17-slim-buster

WORKDIR /app

# copy only the artifacts we need from the first stage
COPY --from=MAVEN_BUILD  /app/target/library-system-v1.jar /app/library-system-v1.jar

# Create app user
RUN groupadd -g 10000 appuser
RUN useradd --home-dir /app/ -u 10000 -g appuser appuser

# Grant app user the necessary rights
RUN chmod -R 0766 /app/
RUN chown -R appuser:appuser /app/
RUN chmod g+w /etc/passwd

# Expose the port that the Spring Boot application is listening on
EXPOSE 8080

# Switch to app user
USER appuser

# set the startup command to execute the jar
CMD ["java", "-jar", "/app/library-system-v1.jar"]
