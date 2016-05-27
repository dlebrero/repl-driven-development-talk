@echo off
cd war
IF "%1"=="-d" (
   set MAVEN_OPTS=%MAVEN_OPTS% -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
)
call mvn clean install -Pdevrun