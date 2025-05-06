FROM maven:3.9.9-eclipse-temurin-24-alpine AS build
WORKDIR /user-auth
COPY . .
RUN mvn dependency:go-offline -B
RUN mvn package -DskipTests -B

FROM openjdk:24-bullseye
WORKDIR /user-auth
COPY --from=build /user-auth/target/*.jar ./user-auth.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "user-auth.jar"]