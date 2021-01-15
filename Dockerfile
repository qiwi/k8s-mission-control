# SPA BUILDER CONTAINER
FROM node:14 AS spabuild

COPY spa/package.json spa/yarn.lock spa/.npmrc /app/
WORKDIR /app
RUN yarn

COPY spa/ /app/
RUN yarn build

# MAIN CONTAINER
FROM openjdk:11-alpine

ENV JAVA_OPTS='' \
    JAVA_ARGS='' \
    JAVA_MAIN_CLASS='' \
    SPRING_CONFIG_LOCATION_PARAMETER_NAME='spring.config.additional-location' \
    JAVA_APP_JAR='devops-mission-control.jar'

COPY build/libs/devops-mission-control.jar $APP_DIR/

ENV SPA_DIR='/opt/mission-control-spa'

COPY --from=spabuild /app/build $SPA_DIR/

EXPOSE 10000

USER ${USER_UID}