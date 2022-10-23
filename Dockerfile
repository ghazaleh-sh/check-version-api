FROM adoptopenjdk/openjdk11

VOLUME /tmp

ENV TZ=Asia/Tehran

RUN  mkdir -p /var/log/check-version-api
RUN  chmod -R 777 /var/log/check-version-api

COPY target/*.jar check-version-api-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-Xdebug","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:1716","-jar","/check-version-api-0.0.1-SNAPSHOT.jar"]


##FROM tomcat:10.0.0-jdk11-openjdk
#FROM tomcat:latest
#USER root
#ARG DEBIAN_FRONTEND=noninteractive
#RUN apt-get update \
#    && apt-get -yq  install apt-utils \
#    && apt-get -yq  install curl \
#    && apt-get -yq  install telnet \
#    && apt-get -yq  install net-tools
#
#ADD target/check-version-api.war /usr/local/tomcat/webapps/
#RUN sed -i 's/port="1716"/port="9080"/' /usr/local/tomcat/conf/server.xml
##ARG JAVA_OPTS=-Dspring.config.location=/opt/configs/check-version-api/
##RUN echo "Env variable :  $JAVA_OPTS"
#ENV TZ=Asia/Tehran
#ENV JPDA_TRANSPORT="dt_socket"
#ENV JPDA_ADDRESS="*:1716"
## unComment for operation version
##ENV JAVA_OPTS="-Dspring.config.location=/opt/configs/check-version-api/"
#ENV CATALINA_OPTS="-Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=1716 -Dcom.sun.management.jmxremote.rmi.port=1716 -Dcom.sun.management.jmxremote.local.only=false"
#
#EXPOSE 9080 1716
#CMD ["catalina.sh" , "jpda", "run"]

