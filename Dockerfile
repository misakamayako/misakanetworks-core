# 使用官方 Gradle 8.5 和 JDK 21 的镜像
FROM gradle:8.5.0-jdk21

# 设置工作目录
WORKDIR /workspace

# 将项目代码复制到容器中
COPY . /workspace

# 创建默认用户
RUN useradd -ms /bin/bash misaka
USER misaka

# 暴露 Spring Boot 默认端口和调试端口
EXPOSE 8080 5005

# 默认启动命令
CMD ["./gradlew", "bootRun"]
