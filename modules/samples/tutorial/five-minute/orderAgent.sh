#!/bin/sh

case "`uname`" in
  Darwin*) if [ -z "$JAVA_HOME" ] ; then
             JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Home
           fi
           ;;
esac


if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD=`which java 2> /dev/null `
    if [ -z "$JAVACMD" ] ; then
        JAVACMD=java
    fi
  fi
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly."
  echo "  We cannot execute $JAVACMD"
  exit 1
fi

CLASSPATH="classes:\
../../lib/jetm-optional.jar:\
../../lib/jetm.jar:\
lib/spring.jar:\
lib/cglib-nodep-2.1_3.jar:\
lib/commons-logging.jar:\
lib/log4j.jar:\
lib/xerces.jar:\
lib/xml-apis.jar"

export CLASSPATH

$JAVACMD etm.tutorial.fiveminute.client.OrderClient "$@"