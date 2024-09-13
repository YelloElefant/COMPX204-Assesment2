FROM ubuntu
RUN apt-get update -y
COPY ./*.class /webServer
WORKDIR /webServer
RUN apt install default-jre -y
CMD ["java", "-cp", "/webServer", "HttpServer"]