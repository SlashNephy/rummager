FROM gradle:7.5.0-jdk17 AS cache
WORKDIR /app
ENV GRADLE_USER_HOME /app/gradle
COPY *.gradle.kts gradle.properties /app/
RUN gradle shadowJar --parallel --console=verbose

FROM gradle:7.5.0-jdk17 AS build
WORKDIR /app
COPY --from=cache /app/gradle /home/gradle/.gradle
COPY *.gradle.kts gradle.properties /app/
COPY src/main/ /app/src/main/
RUN gradle shadowJar --parallel --console=verbose

FROM amazoncorretto:18.0.2 as runtime
WORKDIR /app

COPY --from=build /app/build/libs/rummager-all.jar /app/rummager.jar

ENTRYPOINT ["java", "-jar", "/app/rummager.jar"]
