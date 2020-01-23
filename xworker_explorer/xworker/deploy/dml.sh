#!/bin/bash

DIRNAME=`dirname "$0"`
PROGNAME=`basename "$0"`
GREP="grep"

# Force use GTK2
export SWT_GTK3=0

# Use the maximum available, or set MAX_FD != -1 to use that
MAX_FD="maximum"

# OS specific support (must be 'true' or 'false').
cygwin=false;
darwin=false;
linux=false;
case "`uname`" in
    CYGWIN*)
        cygwin=true
        ;;

    Darwin*)
        darwin=true
        ;;

    Linux)
        linux=true
        ;;
esac

JAVA_OPTS=""
# JVM memory allocation pool parameters - modify as appropriate.
# JAVA_OPTS="-XstartOnFirstThread -Xms512M -Xmx2048M -XX:MaxPermSize=256M"

# Reduce the RMI GCs to once per hour for Sun JVMs.
JAVA_OPTS="$JAVA_OPTS -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -Djava.net.preferIPv4Stack=true  -Dfile.encoding=UTF-8"

# Sample JPDA settings for remote socket debugging
# JAVA_OPTS="$JAVA_OPTS -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n"

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin ; then
    [ -n "$XWORKER_HOME" ] &&
        XWORKER_HOME=`cygpath --unix "$XWORKER_HOME"`
    [ -n "$JAVA_HOME" ] &&
        JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
    [ -n "$JAVAC_JAR" ] &&
        JAVAC_JAR=`cygpath --unix "$JAVAC_JAR"`
fi

# Setup XWORKER_HOME
RESOLVED_XWORKER_HOME=`cd "$DIRNAME"; pwd`
if [ "x$XWORKER_HOME" = "x" ]; then
    # get the full path (without any relative bits)
    XWORKER_HOME=$RESOLVED_XWORKER_HOME
else
 SANITIZED_XWORKER_HOME=`cd "$XWOKRER"; pwd`
 if [ "$RESOLVED_XWORKER_HOME" != "$SANITIZED_XWORKER_HOME" ]; then
   echo "WARNING XWORKER_HOME may be pointing to a different installation - unpredictable results may occur."
   echo ""
 fi
fi
export XWORKER_HOME

configSh="$DIRNAME/dml.conf.sh"
if [ -f "$configSh" ]; then
   source "$configSh"
fi

LOCALCLASSPATH="$XWORKER_HOME/config/:$XWORKER_HOME/xworker.jar"
# Explicitly add javac path to classpath, assume JAVA_HOME set
# properly in rpm mode
if [ -f "$JAVA_HOME/lib/tools.jar" ] ; then
  LOCALCLASSPATH="$LOCALCLASSPATH:$JAVA_HOME/lib/tools.jar"
fi
if [ -f "$JAVA_HOME/lib/classes.zip" ] ; then
  LOCALCLASSPATH="$LOCALCLASSPATH:$JAVA_HOME/lib/classes.zip"
fi

# Setup the JVM
if [ "x$JAVA" = "x" ]; then
    if [ "x$JAVA_HOME" != "x" ]; then
        JAVA="$JAVA_HOME/bin/java"
    else
        JAVA="java"
    fi
fi

if [ "$PRESERVE_JAVA_OPTS" != "true" ]; then
    # Check for -d32/-d64 in JAVA_OPTS
    JVM_D64_OPTION=`echo $JAVA_OPTS | $GREP "\-d64"`
    JVM_D32_OPTION=`echo $JAVA_OPTS | $GREP "\-d32"`

    # Check If server or client is specified
    SERVER_SET=`echo $JAVA_OPTS | $GREP "\-server"`
    CLIENT_SET=`echo $JAVA_OPTS | $GREP "\-client"`

    if [ "x$JVM_D32_OPTION" != "x" ]; then
        JVM_OPTVERSION="-d32"
    elif [ "x$JVM_D64_OPTION" != "x" ]; then
        JVM_OPTVERSION="-d64"
    elif $darwin && [ "x$SERVER_SET" = "x" ]; then
        # Use 32-bit on Mac, unless server has been specified or the user opts are incompatible
        "$JAVA" -d32 $JAVA_OPTS -version > /dev/null 2>&1 && PREPEND_JAVA_OPTS="-d32" && JVM_OPTVERSION="-d32"
    fi

    CLIENT_VM=false
    if [ "x$CLIENT_SET" != "x" ]; then
        CLIENT_VM=true
    elif [ "x$SERVER_SET" = "x" ]; then
        if $darwin && [ "$JVM_OPTVERSION" = "-d32" ]; then
            # Prefer client for Macs, since they are primarily used for development
            CLIENT_VM=true
            PREPEND_JAVA_OPTS="$PREPEND_JAVA_OPTS -client"
        else
            PREPEND_JAVA_OPTS="$PREPEND_JAVA_OPTS -server"
        fi
    fi

    if [ $CLIENT_VM = false ]; then
        NO_COMPRESSED_OOPS=`echo $JAVA_OPTS | $GREP "\-XX:\-UseCompressedOops"`
        if [ "x$NO_COMPRESSED_OOPS" = "x" ]; then
            "$JAVA" $JVM_OPTVERSION -server -XX:+UseCompressedOops -version >/dev/null 2>&1 && PREPEND_JAVA_OPTS="$PREPEND_JAVA_OPTS -XX:+UseCompressedOops"
        fi

        NO_TIERED_COMPILATION=`echo $JAVA_OPTS | $GREP "\-XX:\-TieredCompilation"`
        if [ "x$NO_TIERED_COMPILATION" = "x" ]; then
            "$JAVA" $JVM_OPTVERSION -server -XX:+TieredCompilation -version >/dev/null 2>&1 && PREPEND_JAVA_OPTS="$PREPEND_JAVA_OPTS -XX:+TieredCompilation"
        fi
    fi

    JAVA_OPTS="$PREPEND_JAVA_OPTS $JAVA_OPTS"
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
    XWORKER_HOME=`cygpath --path --windows "$XWORKER_HOME"`
    JAVA_HOME=`cygpath --path --windows "$JAVA_HOME"`
    XWOKRER_CLASSPATH=`cygpath --path --windows "$XWOKRER_CLASSPATH"`
    XWOKRER_ENDORSED_DIRS=`cygpath --path --windows "$XWOKRER_ENDORSED_DIRS"`
    XWOKRER_MODULEPATH=`cygpath --path --windows "$XWOKRER_MODULEPATH"`
fi

# Display our environment
# echo "========================================================================="
# echo ""
# echo "  XWorker Bootstrap Environment"
# echo ""
# echo "  XWORKER_HOME: $XWORKER_HOME"
# echo ""
# echo "  JAVA: $JAVA"
# echo ""
# echo "  JAVA_OPTS: $JAVA_OPTS"
# echo ""
# echo "  LOCALCLASSPATH: $LOCALCLASSPATH"
# echo ""
# echo "========================================================================="
# echo ""

while true; do
   if [ "x$LAUNCH_XWORKER_IN_BACKGROUND" = "x" ]; then
      # Execute the JVM in the foreground
      eval \"$JAVA\" $JAVA_OPTS \
         -classpath \"$LOCALCLASSPATH\" \
         xworker.startup.Startup "$XWORKER_HOME" "$@"
      XWORKER_STATUS=$?
   else
      # Execute the JVM in the background
      eval \"$JAVA\" -classpath \"$LOCALCLASSPATH\" $JAVA_OPTS \          
         xworker.startup.Startup "$XWORKER_HOME" "$@" "&"
     XWORKER_PID=$!
      # Trap common signals and relay them to the xworker process
      trap "kill -HUP  $XWORKER_PID" HUP
      trap "kill -TERM $XWORKER_PID" INT
      trap "kill -QUIT $XWORKER_PID" QUIT
      trap "kill -PIPE $XWORKER_PID" PIPE
      trap "kill -TERM $XWORKER_PID" TERM
      if [ "x$XWORKER_PIDFILE" != "x" ]; then
        echo $XWORKER_PID > $XWORKER_PIDFILE
      fi
      # Wait until the background process exits
      WAIT_STATUS=128
      while [ "$WAIT_STATUS" -ge 128 ]; do
         wait $XWORKER_PID 2>/dev/null
         WAIT_STATUS=$?
         if [ "$WAIT_STATUS" -gt 128 ]; then
            SIGNAL=`expr $WAIT_STATUS - 128`
            SIGNAL_NAME=`kill -l $SIGNAL`
            echo "*** XWorker process ($XWORKERPID) received $SIGNAL_NAME signal ***" >&2
         fi
      done
      if [ "$WAIT_STATUS" -lt 127 ]; then
         XWORKER_STATUS=$WAIT_STATUS
      else
         XWORKER_STATUS=0
      fi
      if [ "$XWORKER_STATUS" -ne 10 ]; then
            # Wait for a complete shudown
            wait $XWORKER_PID 2>/dev/null
      fi
      if [ "x$XWORKER_PIDFILE" != "x" ]; then
            grep "$JXWORKER_PID" $XWORKER_PIDFILE && rm $XWORKER_PIDFILE
      fi
   fi
   if [ "$XWORKER_STATUS" -eq 10 ]; then
      echo "Restarting XWorker..."
   else
      exit $XWORKER_STATUS
   fi
done
