# ===============================
# 1️⃣ Build Stage (Gradle Build)
# ===============================
FROM gradle:8.10.2-jdk17 AS builder

WORKDIR /app

# Gradle 설정 파일 복사 (캐시 최적화)
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# 의존성 미리 다운로드
RUN gradle dependencies --no-daemon || return 0

# 나머지 전체 복사
COPY . .

# jar 빌드
RUN gradle clean build -x test --no-daemon

# ===============================
# 2️⃣ Runtime Stage (실행용)
# ===============================
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# builder 단계에서 빌드된 jar 가져오기
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
