#FROM openjdk:8-jdk-alpine
FROM graalvm-ce:22.0.0.2

MAINTAINER chendk "yingmi@yingmi.cn"

VOLUME /data/log

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo 'Asia/Shanghai' >/etc/timezone && \
    sh -c "mkdir -p /app"

ENV JVM_OPTS -server -Xms512m -Xmx512m
ENV INSTANCES 2

#切换镜像目录，进入/data/app目录
WORKDIR /app
COPY app-jar-with-dependencies.jar /app/app.jar
#公开端口
EXPOSE 9011

#设置启动命令
ENTRYPOINT java $JVM_OPTS -jar app.jar