# Construïm amb Gradle i distribuïm només l'aplicació i les dependències.
FROM gradle:9.7.1-jdk21 AS construccio
WORKDIR /home/gradle/project
RUN chown gradle:gradle /home/gradle/project
COPY --chown=gradle:gradle settings.gradle.kts build.gradle.kts ./
COPY --chown=gradle:gradle src ./src
USER gradle
RUN gradle --no-daemon installDist

FROM eclipse-temurin:21-jre-jammy
RUN groupadd --system ribera && useradd --system --gid ribera --uid 10001 --create-home ribera
WORKDIR /app
COPY --from=construccio --chown=ribera:ribera /home/gradle/project/build/install/ribera-api/ ./
USER ribera
EXPOSE 8080
ENTRYPOINT ["/app/bin/ribera-api"]
