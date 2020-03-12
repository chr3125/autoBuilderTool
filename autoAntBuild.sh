#!/bin/sh

JAVA_HOME=$(pwd)/application/jdk1.7.0_80
export JAVA_HOME
echo $JAVA_HOME

ANT_HOME=$(pwd)/application/apache-ant-1.9.14-bin
export ANT_HOME
echo $ANT_HOME

exec $ANT_HOME/bin/ant -f  $(pwd)/application/ROOT deploy
