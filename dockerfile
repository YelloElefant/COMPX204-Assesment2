FROM ubuntu
EXPOSE 80
RUN apt-get update -y
RUN mkdir /webServer
COPY ./*.class /webServer/
WORKDIR /webServer
RUN apt install default-jre -y
RUN cd /webServer
RUN ls 
RUN env
CMD ["java", "HttpServer"]