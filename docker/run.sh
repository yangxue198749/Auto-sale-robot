#!/usr/bin/env bash
wget -O /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/setting.properties $SETTING_URL
sh /usr/local/tomcat/bin/catalina.sh run
