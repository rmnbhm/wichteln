# Cf. https://spring.io/guides/topicals/spring-boot-docker/#_a_better_dockerfile

FROM adoptopenjdk/openjdk11:alpine as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Skip tests
RUN ./mvnw package -DskipTests

RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM adoptopenjdk/openjdk11:alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.rmnbhm.wichteln.WichtelnApplication"]
