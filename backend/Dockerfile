# Optimized Build Stage: Use pre-built JAR to save EC2 memory
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
# 로컬에서 빌드된 JAR 파일을 복사합니다.
COPY build/libs/*.jar app.jar
EXPOSE 8080
# EC2 프리티어 메모리 제한을 고려하여 -Xmx 설정
ENTRYPOINT ["java", "-Xmx300m", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
