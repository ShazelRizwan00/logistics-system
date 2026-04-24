@echo off
setlocal

set APP_HOME=%~dp0
set WRAPPER_JAR=%APP_HOME%gradle\wrapper\gradle-wrapper.jar

if exist "%WRAPPER_JAR%" (
  "%JAVA_HOME%\bin\java.exe" -classpath "%WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*
  goto :eof
)

where gradle >nul 2>nul
if %ERRORLEVEL% EQU 0 (
  gradle %*
  goto :eof
)

echo Error: gradle-wrapper.jar is missing and no 'gradle' executable is available in PATH.
echo Add gradle\wrapper\gradle-wrapper.jar or install Gradle globally.
exit /b 1
