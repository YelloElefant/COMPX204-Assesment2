FROM ubuntu
RUN apt-get update -y
COPY ./ /data
RUN mkdir /webServer
WORKDIR /webServer
RUN apt install default-jre -y

RUN javac -cp /webServer /data/*.java
CMD ["java", "-cp", "/webServer", "HttpServer"]