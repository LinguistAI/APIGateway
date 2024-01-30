FROM openjdk:17
ADD target/gateway.jar gateway.jar
ENTRYPOINT ["java","-jar","/gateway.jar"]