#!/bin/sh
JAVA_HOME=$(pwd)/application/jdk1.7.0_80
export JAVA_HOME
echo $JAVA_HOME

exec java -jar $(pwd)/autoApplicationBuild.jar 