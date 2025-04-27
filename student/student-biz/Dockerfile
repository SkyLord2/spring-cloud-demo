FROM openjdk:8-jre
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
EXPOSE 9205
ENTRYPOINT ["java","-jar","/app.jar"]