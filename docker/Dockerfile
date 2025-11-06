# Multi-stage build for Spring Boot application
FROM eclipse-temurin:17-jdk-jammy as builder

WORKDIR /app

# Gradle wrapper와 설정 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 소스 코드 복사
COPY src src

# 빌드 실행
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test --no-daemon

# 실행 이미지
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 헬스체크용 curl 설치
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

