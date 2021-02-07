# SPA BUILDER CONTAINER
FROM node:14 AS spabuild

COPY spa/package.json spa/yarn.lock /app/
WORKDIR /app
RUN yarn

COPY spa/ /app/
RUN yarn build

# JAVA BUILDER CONTAINER
FROM gradle:6.8-jdk11 AS javabuild

ENV GRADLE_OPTS="-Dorg.gradle.daemon=false"

COPY build.gradle.kts spotbugs-filters.xml checkstyle.xml checkstyle-suppressions.xml .editorconfig /app/
WORKDIR /app
RUN gradle build --no-daemon || return 0  # just install dependencies

COPY src/ /app/src
RUN gradle build --no-daemon

# MAIN CONTAINER
FROM adoptopenjdk/openjdk11:alpine

ENV USER=mission-control \
    USER_UID=850 \
    USER_GID=850 \
    APP_DIR='/opt/mission-control' \
    SPA_DIR='/opt/mission-control-spa' \
    CONFIG_DIR='/etc/mission-control' \
    EMBEDDED_CONFIG_DIR='/etc/mission-control/embedded' \
    OVERRIDE_CONFIG_DIR='/etc/mission-control/override' \
    SECRETS_CONFIG_DIR='/etc/mission-control/secrets' \
    JAVA_OPTS=''

COPY --from=javabuild /app/build/libs/mission-control.jar $APP_DIR/
COPY --from=spabuild /app/build $SPA_DIR/
COPY src/main/resources $EMBEDDED_CONFIG_DIR/

RUN mv $EMBEDDED_CONFIG_DIR/application-example.yml $EMBEDDED_CONFIG_DIR/application.yml && \
    mv $EMBEDDED_CONFIG_DIR/bootstrap-example.yml $EMBEDDED_CONFIG_DIR/bootstrap.yml && \
    mkdir -p "${OVERRIDE_CONFIG_DIR}" "${SECRETS_CONFIG_DIR}" && \
    addgroup -S -g "${USER_GID}" "${USER}" && \
    adduser  -S -u "${USER_UID}" -G "${USER}" -s /bin/false "${USER}" && \
    chown -R "${USER}:${USER}" "${APP_DIR}" "${SPA_DIR}" "${CONFIG_DIR}" && \
    chmod -R 774 "${APP_DIR}" "${SPA_DIR}" "${CONFIG_DIR}"

USER mission-control

EXPOSE 8080

CMD java -Dspring.config.location="${EMBEDDED_CONFIG_DIR}/,${OVERRIDE_CONFIG_DIR}/,${SECRETS_CONFIG_DIR}/" \
         ${JAVA_OPTS} \
         -jar "${APP_DIR}/mission-control.jar"