FROM openjdk:11
ADD ./disk1-0.0.1-SNAPSHOT.jar disk1-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar","disk1-0.0.1-SNAPSHOT.jar"]
EXPOSE 80

