FROM ubuntu
EXPOSE 80
RUN apt-get update -y
RUN mkdir /webServer
COPY ./*.class /webServer
ENV HOSTIP="192.168.1.29"
WORKDIR /webServer
RUN apt install default-jre -y
RUN cd /webServer
RUN ls 
CMD ["java", "HttpServer"]