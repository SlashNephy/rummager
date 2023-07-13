FROM gradle:8.2.1-jdk17@sha256:a0d6bf06cc83ae6d1a08d50ef9f4d1c172630bc7152a3857b6e78783cd334494 AS cache
WORKDIR /app
ENV GRADLE_USER_HOME /app/gradle
COPY *.gradle.kts gradle.properties /app/
RUN gradle shadowJar --parallel --console=verbose

FROM gradle:8.2.1-jdk17@sha256:a0d6bf06cc83ae6d1a08d50ef9f4d1c172630bc7152a3857b6e78783cd334494 AS build
WORKDIR /app
COPY --from=cache /app/gradle /home/gradle/.gradle
COPY *.gradle.kts gradle.properties /app/
COPY src/main/ /app/src/main/
RUN gradle shadowJar --parallel --console=verbose

FROM amazoncorretto:18.0.2 as runtime
WORKDIR /app

COPY --from=build /app/build/libs/rummager-all.jar /app/rummager.jar

ENTRYPOINT ["java", "-jar", "/app/rummager.jar"]
