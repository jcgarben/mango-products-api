# =========================
# Stage 1: Build with Maven + Corretto 21
# =========================
FROM maven:3.9.9-amazoncorretto-21 AS build
WORKDIR /app

# Copy only pom.xml first to leverage Docker cache for dependencies and OpenAPI spec
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Now copy the rest of the source code
COPY src ./src

# Compile and generate JAR without tests (to speed up)
RUN mvn clean package -DskipTests

# =========================
# Stage 2: Run with Corretto 21
# =========================
FROM amazoncorretto:21
WORKDIR /app

# Install curl for healthcheck
RUN yum install -y curl && yum clean all

# Copy JAR generated from build stage
COPY --from=build /app/target/products-api-0.0.1-SNAPSHOT.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
