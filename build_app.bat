@echo off
cd /d "%~dp0"
set JAVA_HOME=c:\Users\yorke\Downloads\MonitorPC Basic\jdk
call gradlew.bat assembleDebug
