#!/usr/bin/env sh

# Lightweight Gradle launcher for environments where wrapper artifacts are absent.
# If gradle-wrapper.jar is present, delegate to the standard wrapper bootstrap.

set -eu

APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

if [ -f "$WRAPPER_JAR" ]; then
  if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA_BIN="$JAVA_HOME/bin/java"
  else
    JAVA_BIN="$(command -v java)"
  fi
  exec "$JAVA_BIN" -classpath "$WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
fi

if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi

echo "Error: gradle-wrapper.jar is missing and no 'gradle' executable is available in PATH." >&2
echo "Add gradle/wrapper/gradle-wrapper.jar or install Gradle globally." >&2
exit 1
