#!/bin/bash -e

set -x

if [ -z "$1" ]
  then
    echo "Missing parameters. Please enter the [Scala version]."
    echo "sbt-build-test.sh 2.13.6"
    exit 1
else

  scala_version=$1
  sbt_params="$2"
  echo "============================================"
  echo "Testing projects"
  echo "--------------------------------------------"
  echo ""

  sbt \
    -J-XX:MaxMetaspaceSize=1024m \
    -J-Xmx2048m \
    ++${scala_version}! \
    -v "${sbt_params}" \
    clean \
    test

  echo "============================================"
  echo "Testing projects: Done"
  echo "============================================"
fi
