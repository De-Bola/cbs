FROM openjdk:17

ARG JAR_FILE=cbs/build/libs/cbs-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} cbs.jar

ENTRYPOINT ["java","-jar","/cbs.jar"]
