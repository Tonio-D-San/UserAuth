FROM maven:3.9.9-eclipse-temurin-24-alpine AS build
WORKDIR /user-auth
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:24-bullseye
WORKDIR /user-auth
COPY --from=build /user-auth/target/user-auth.jar ./user-auth.jar
ENTRYPOINT ["java", "-jar", "user-auth.jar"]