FROM adoptopenjdk:16-jdk-hotspot as build
WORKDIR /app
COPY . .
RUN ./gradlew build

FROM adoptopenjdk:16-jre-hotspot
WORKDIR /app

COPY --from=build /app/build/libs/Antifraud.jar app.jar

EXPOSE 28852

CMD ["java", "-jar", "app.jar"]