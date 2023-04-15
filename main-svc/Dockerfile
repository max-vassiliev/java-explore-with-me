FROM azul/zulu-openjdk-alpine:11-latest
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8081
COPY target/*.jar ewm-service.jar
ENTRYPOINT ["java","-jar","/ewm-service.jar"]
