@echo off
cls
echo Starting Tioga Solution's Push Server
set push-server-version=unknown
%JAVA_HOME%\bin\java.exe -jar ./lib/tioga-jobs-agent-%push-server-version%.jar serverName localhost
