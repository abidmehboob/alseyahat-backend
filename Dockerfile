FROM maven:3.8.4-openjdk-8

RUN rm -rf /usr/share/doc/*
RUN rm -rf /usr/share/man/*

WORKDIR /app

COPY pom.xml .
COPY src .

RUN mvn clean package

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "target/alseyahat-0.0.1-SNAPSHOT.jar"]
