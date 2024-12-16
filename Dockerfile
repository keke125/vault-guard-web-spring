FROM eclipse-temurin:21-jre

COPY target/*.jar /app/vgw.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/vgw.jar"]