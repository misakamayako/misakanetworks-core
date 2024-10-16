# 使用官方 Gradle 8.5 和 JDK 21 的镜像
FROM gradle:8.5.0-jdk21
# 设置工作目录
WORKDIR /workspace
# 复制项目依赖文件
COPY . .
# 构建依赖
RUN ./gradlew dependencies
