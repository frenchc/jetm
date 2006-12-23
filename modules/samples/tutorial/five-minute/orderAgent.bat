if not "%JAVA_HOME%" == "" goto JAVA_HOME_AVAILABLE
set JAVACMD=java
goto STARTUP

:JAVA_HOME_AVAILABLE

set JAVACMD=%JAVA_HOME%\bin\java

:STARTUP

set CLASSPATH="classes;..\..\lib\jetm-optional.jar;..\..\lib\jetm.jar;lib\spring.jar;lib\cglib-nodep-2.1_3.jar;lib\commons-logging.jar;lib\log4j.jar;lib\xerces.jar;lib\xml-apis.jar"

%JAVACMD% etm.tutorial.fiveminute.client.OrderClient %*