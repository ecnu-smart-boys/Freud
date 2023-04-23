FROM openjdk:8-jre

ARG WORKDIR=/opt/freud

WORKDIR ${WORKDIR}
COPY target/lib ./lib

EXPOSE 5508

ENV TZ=Asia/Shanghai
ENV SPRING_PROFILES_ACTIVE=prod

COPY target/*.jar freud.jar

ENTRYPOINT java -jar freud.jar