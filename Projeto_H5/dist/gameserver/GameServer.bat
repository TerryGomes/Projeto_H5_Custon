@echo off
title L2Age Game Server Console
:start
echo Starting GameServer.
echo.

set JAVA_OPTS=%JAVA_OPTS% -Xmn128m
set JAVA_OPTS=%JAVA_OPTS% -Xms2G
set JAVA_OPTS=%JAVA_OPTS% -Xmx4G

java -server %JAVA_OPTS% -Dfile.encoding=UTF-8 -cp config;./../libs/* l2f.gameserver.GameServer

if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Server restarted ...
echo.
goto start
:error
echo.
echo Server terminated abnormaly ...
echo.
:end
echo.
echo Server terminated ...
echo.

pause
