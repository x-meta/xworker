@echo off
rem -------------------------------------------------------------------------
rem XWorker Bootstrap Script for Win32
rem -------------------------------------------------------------------------

rem Modify charset to utf-8
rem CHCP 65001

@if not "%ECHO%" == ""  echo %ECHO%
@if "%OS%" == "Windows_NT"  setlocal

set DIRNAME=.\
if "%OS%" == "Windows_NT" set DIRNAME=%~dp0%
set PROGNAME=run.bat
if "%OS%" == "Windows_NT" set PROGNAME=%~nx0%

pushd %DIRNAME%
set XWORKER_HOME=%CD%\
popd

rem Read all command line arguments

REM
REM The %ARGS% env variable commented out in favor of using %* to include
REM all args in java command line. See bug #840239. [jpl]
REM
REM set ARGS=
REM :loop
REM if [%1] == [] goto endloop
REM         set ARGS=%ARGS% %1
REM         shift
REM         goto loop
REM :endloop

rem Find run.jar, or we can't continue

set RUNJAR=%XWORKER_HOME%\lib\
if exist "%RUNJAR%" goto FOUND_RUN_JAR
echo Could not locate %RUNJAR%. Please check that you are in the
echo bin directory when running this script.
goto END

:FOUND_RUN_JAR
@setlocal enableextensions enabledelayedexpansion
REM @set classpath=.;xworker.jar;
REM @for %%c in (lib/*.jar) do @set classpath=!classpath!;lib/%%c
REM @for %%c in (lib/db/*.jar) do @set classpath=!classpath!;lib/db/%%c
rem @echo %classpath%
@set RUNJAR=%classpath%%XWORKER_HOME%startup.jar;./bin
rem @echo %RUNJAR%
if not "%JAVA_HOME%" == "" goto ADD_TOOLS

set JAVA=java
echo JAVA_HOME is not set.  Unexpected results may occur.
echo Set JAVA_HOME to the directory of your local JDK to avoid this message.
goto SKIP_TOOLS

:ADD_TOOLS

set JAVA=%JAVA_HOME%\bin\java

rem A full JDK with toos.jar is not required anymore since XWORKER web packages
rem the eclipse jdt compiler and javassist has its own internal compiler.
if not exist "%JAVA_HOME%\lib\tools.jar" goto SKIP_TOOLS

rem If exists, point to the JDK javac compiler in case the user wants to
rem later override the eclipse jdt compiler for compiling JSP pages.
set JAVAC_JAR=%JAVA_HOME%\lib\tools.jar

:SKIP_TOOLS

rem If XWORKER_CLASSPATH or JAVAC_JAR is empty, don't include it, as this will 
rem result in including the local directory in the classpath, which makes
rem error tracking harder.
if not "%JAVAC_JAR%" == "" set RUNJAR=%JAVAC_JAR%;%RUNJAR%
if "%XWORKER_CLASSPATH%" == "" set RUN_CLASSPATH=%RUNJAR%
if "%RUN_CLASSPATH%" == "" set RUN_CLASSPATH=%XWORKER_CLASSPATH%;%RUNJAR%

set XWORKER_CLASSPATH=%RUN_CLASSPATH%

rem Setup XWORKER specific properties
set JAVA_OPTS=%JAVA_OPTS% -Dprogram.name=%PROGNAME%  -Dfile.encoding=UTF-8

rem JVM memory allocation pool parameters. Modify as appropriate.
rem set JAVA_OPTS=%JAVA_OPTS% -Xms512m -Xmx2048m

rem With Sun JVMs reduce the RMI GCs to once per hour
set JAVA_OPTS=%JAVA_OPTS% -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000

rem JPDA options. Uncomment and modify as appropriate to enable remote debugging.
rem set JAVA_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=y %JAVA_OPTS%

rem Setup the java endorsed dirs
rem set XWORKER_ENDORSED_DIRS=%XWORKER_HOME%\lib\endorsed

call "%XWORKER_HOME%\dml.conf.cmd"

rem echo ===============================================================================
rem echo.
rem echo   XWORKER Bootstrap Environment
rem echo.
rem echo   XWORKER_HOME: %XWORKER_HOME%
rem echo.
rem echo   JAVA: %JAVA%
rem echo.
rem echo   JAVA_OPTS: %JAVA_OPTS%
rem echo.
rem echo   CLASSPATH: %XWORKER_CLASSPATH%
rem echo.
rem echo ===============================================================================
rem echo.

:RESTART
"%JAVA%" %JAVA_OPTS% "-Djava.endorsed.dirs=%XWORKER_ENDORSED_DIRS%" -classpath "%XWORKER_CLASSPATH%;%XWORKER_HOME%xworker.jar" xworker.startup.Startup  %XWORKER_HOME% %*
if ERRORLEVEL 10 goto RESTART

:END
if "%NOPAUSE%" == "xx" pause

:END_NO_PAUSE