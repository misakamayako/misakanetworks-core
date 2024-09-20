# 使用 Gradle 8.5 和 JDK 21 的基础镜像进行构建
FROM gradle:8.5-jdk21 AS build

# 设置工作目录
WORKDIR /app

# 将项目代码复制到容器中
COPY . .

# 使用 Gradle 构建项目，生成可执行 JAR 文件
RUN ./gradlew clean build -x test

# 使用较小的基础镜像作为运行时环境
FROM openjdk:21-jdk-slim AS runtime

# 设置工作目录
WORKDIR /app

# 从构建阶段复制可执行的 JAR 文件
COPY --from=build /app/build/libs/*.jar app.jar

# 设置环境变量（可选）
ENV SPRING_PROFILES_ACTIVE=prod

# 暴露应用端口
EXPOSE 8080

# 默认命令
ENTRYPOINT ["java", "-jar", "app.jar"]
