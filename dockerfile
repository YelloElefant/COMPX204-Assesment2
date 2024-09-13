FROM ubuntu
RUN apt-get update -y
COPY ./*.class /webServer
WORKDIR /webServer
RUN apt install default-jre -y
RUN cd /webServer
RUN ls 
CMD ["java", "HttpServer"]