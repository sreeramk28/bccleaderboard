FROM eclipse-temurin:17-alpine as base
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src

FROM base as build
RUN ./mvnw clean package

FROM eclipse-temurin:17-alpine as production
EXPOSE 8080
COPY --from=build /app/target/bccleaderboard-0.0.1-SNAPSHOT.jar /bccleaderboard.jar
ENTRYPOINT ["java", "-jar", "/bccleaderboard.jar"]