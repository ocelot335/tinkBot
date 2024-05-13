FROM eclipse-temurin:21-jre-jammy

COPY target/scrapper.jar app.jar

EXPOSE 8080
EXPOSE 8081

ENTRYPOINT [ "java", "-jar", "app.jar" ]
