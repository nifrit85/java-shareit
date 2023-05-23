FROM amazoncorretto:11-alpine-jdk
ENV JAVA_TOOL_OPTIONS = ""
COPY target/*.jar server.jar
ENTRYPOINT ["java","-jar","/server.jar"]