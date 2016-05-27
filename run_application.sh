#!/bin/sh
cd war

if [ "$1" = '-d' ]; then
   MAVEN_OPTS="$MAVEN_OPTS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
fi

mvn clean install -Pdevrun
