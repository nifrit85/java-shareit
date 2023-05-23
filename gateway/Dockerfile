FROM amazoncorretto:11-alpine-jdk
ENV JAVA_TOOL_OPTIONS = ""
COPY target/*.jar gateway.jar
ENTRYPOINT ["java","-jar","/gateway.jar"]
