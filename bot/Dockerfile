FROM eclipse-temurin:21-jre-jammy

COPY target/bot.jar app.jar

EXPOSE 8090
EXPOSE 8091

ENTRYPOINT [ "java", "-jar", "app.jar" ]
