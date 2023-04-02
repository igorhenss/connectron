FROM openjdk:20
LABEL MAINTAINER="github.com/igorhenss"

COPY target/connectron.jar connectron.jar

ENTRYPOINT ["java", "-jar", "/connectron.jar"]
EXPOSE 8080
