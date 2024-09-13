FROM ubuntu
RUN apt-get update
COPY ./ /data
RUN mkdir /webServer
WORKDIR /webServer
RUN apt install default-jre

RUN javac -cp /webServer /data/*.java
CMD ["java", "-cp", "/webServer", "HttpServer"]