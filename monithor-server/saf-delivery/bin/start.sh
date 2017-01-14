#!/bin/sh

$JAVA_HOME/bin/java -Dloader.path="config/*" -jar lib/monithor-server.jar --spring.config.location=config/application.properties
