FROM openjdk:11-jdk-slim
ARG JAR_FILE=build/libs/seatchoice-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar","/app.jar"]