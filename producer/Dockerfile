# temp build
FROM gradle:6.3-jdk8 as build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
# no need to run tests here as tests is a separate part of the CI
RUN gradle build --no-daemon -x test

# final build - this contains only the assets needed by the final app
FROM openjdk:8-jre-slim
RUN mkdir /app
WORKDIR /app

ENV APP_NAME=producer-0.0.1-SNAPSHOT.jar
COPY --from=build /home/gradle/src/build/libs/${APP_NAME}  /app
ENTRYPOINT java -jar /app/${APP_NAME}
EXPOSE 8080
