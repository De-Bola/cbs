# run gradle clean build
# run docker build -t cbs .
# docker-compose up --build
# create tables in postgres

# will try this simpler command later
FROM openjdk:17

ARG JAR_FILE=cbs/build/libs/cbs-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} cbs.jar

ENTRYPOINT ["java","-jar","/cbs.jar"]
