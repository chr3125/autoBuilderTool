@echo on
set JAVA_HOME =%~dp0application\jdk1.8.0_121
set ANT_HOME=%~dp0application\apache-ant-1.9.14-bin
set path=%path%;%JAVA_HOME%\bin;%ANT_HOME%\bin;

%ANT_HOME%\bin\ant -f  %~dp0application\ROOT deploy

pause
