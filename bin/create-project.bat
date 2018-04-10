@echo off
echo --------------------------------------------
echo  Create Titan Module Batch
for /f "tokens=2 delims==" %%a in ('wmic path win32_operatingsystem get LocalDateTime /value') do (
	set t=%%a
)
set Today=%t:~0,4%-%t:~4,2%-%t:~6,2%
echo  @version 1.0.%Today%
echo  @author Administrator
echo --------------------------------------------
set /p systemName=Please insert your Titan Module English name£º
echo --------------------------------------------
echo  Titan Module creating£¬Please wait...
echo --------------------------------------------
md  %systemName%\src\main\java\com\mybank\bkcommon\collector\%systemName%\collector
md  %systemName%\src\main\java\com\mybank\bkcommon\collector\%systemName%\model
md  %systemName%\src\main\resources\META-INF\spring
md  %systemName%\src\main\resources\view
md  %systemName%\src\test\java\com\mybank\bkcommon\collector\%systemName%
md  %systemName%\src\test\resources\
copy project-template\helloworld.vm %systemName%\src\main\resources\view\helloworld.vm
copy project-template\HelloWorld.java %systemName%\src\main\java\com\mybank\bkcommon\collector\%systemName%\model\HelloWorld.java
copy project-template\HelloWorldCollector.java %systemName%\src\main\java\com\mybank\bkcommon\collector\%systemName%\collector\HelloWorldCollector.java
copy project-template\pom-template.xml %systemName%\pom.xml
copy project-template\integration.xml %systemName%\src\main\resources\META-INF\spring\integration.xml
copy project-template\QuickStarter-template.java  %systemName%\src\test\java\com\mybank\bkcommon\collector\%systemName%\QuickStarter.java
copy project-template\integration-test.xml %systemName%\src\test\resources\integration-test.xml
copy project-template\spring-template.xml %systemName%\src\main\resources\META-INF\spring\%systemName%.xml
copy project-template\log4j-template.xml %systemName%\src\test\resources\log4j.xml
copy project-template\log4j-main-template.xml %systemName%\src\main\resources\log4j.xml
setlocal Enabledelayedexpansion
call:changePath %systemName%\src\test\java\com\mybank\bkcommon\collector\%systemName%\QuickStarter.java
call:changePath %systemName%\pom.xml
call:changePath %systemName%\src\test\resources\integration-test.xml
call:changePath %systemName%\src\main\resources\META-INF\spring\%systemName%.xml
call:changePath %systemName%\src\main\java\com\mybank\bkcommon\collector\%systemName%\model\HelloWorld.java
call:changePath %systemName%\src\main\java\com\mybank\bkcommon\collector\%systemName%\collector\HelloWorldCollector.java
call:changePath %systemName%\src\main\resources\log4j.xml
endlocal
echo "Create Titan Module %systemName% Finish"
pause

:changePath
set pacStr=${package_name}
set proStr=${project_name}
set targetStr=%systemName%
for /f "tokens=* delims=£¥" %%i in (%1) do (
    set var=%%i
	set var=!var:%pacStr%=%targetStr%!
	set var=!var:%proStr%=%targetStr%!
	echo !var!>>$
)
move $ %1
goto:eof