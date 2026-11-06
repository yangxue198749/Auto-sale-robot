# Version 1.0.0
FROM registry.cn-beijing.aliyuncs.com/touchfound/tomcat:8.5-jre8
MAINTAINER liyang "liy@kdxfilm.com"

ENV SETTING_URL="http://cf.touchfound.net/docker-config-file/vms/dev/setting.properties"

RUN rm -rf /usr/local/tomcat/webapps/*
ADD ./ROOT/ /usr/local/tomcat/webapps/ROOT/
ADD ./server.xml /usr/local/tomcat/conf/server.xml

EXPOSE 8080
ADD ./run.sh /usr/local/tomcat/bin/run.sh
RUN chmod +x /usr/local/tomcat/bin/run.sh
CMD ["sh", "/usr/local/tomcat/bin/run.sh"]
