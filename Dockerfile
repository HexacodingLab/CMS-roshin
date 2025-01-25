FROM amazoncorretto:23-alpine-full as builder

WORKDIR /app

# Build jar file
COPY . .
RUN sh ./gradlew bootJar

FROM amazoncorretto:23-alpine

# Create data folder
RUN mkdir "/data"
ENV SPRING_DATASOURCE_URL=jdbc:sqlite:/data/database.db

# Copy executable
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# Start app
ENV ROSHIN_USER=admin
ENV ROSHIN_PASSWORD=admin
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
