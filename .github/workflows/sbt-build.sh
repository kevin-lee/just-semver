#!/bin/bash -e

set -x

if [ -z "$1" ]
  then
    echo "Scala version is missing. Please enter the Scala version."
    echo "sbt-build.sh 2.13.5"
    exit 1
else
  scala_version=$1
  echo "============================================"
  echo "Build projects"
  echo "--------------------------------------------"
  echo ""
  : ${CURRENT_BRANCH_NAME:?"CURRENT_BRANCH_NAME is missing."}
  : ${CI_BRANCH:?"CI_BRANCH is missing."}
  export SOURCE_DATE_EPOCH=$(date +%s)
  echo "SOURCE_DATE_EPOCH=$SOURCE_DATE_EPOCH"

  if [[ "$CURRENT_BRANCH_NAME" == "main" || "$CURRENT_BRANCH_NAME" == "release" ]]
  then
    sbt \
      -J-XX:MaxMetaspaceSize=1024m \
      -J-Xmx2048m \
      ++${scala_version}! \
      clean \
      coverage \
      test \
      coverageReport \
      coverageAggregate
    sbt \
      -J-XX:MaxMetaspaceSize=1024m \
      -J-Xmx2048m \
      ++${scala_version}! \
      coveralls
    sbt \
      -J-XX:MaxMetaspaceSize=1024m \
      -J-Xmx2048m \
      ++${scala_version}! \
      clean \
      packagedArtifacts
  else
    sbt \
      -J-XX:MaxMetaspaceSize=1024m \
      -J-Xmx2048m \
      ++${scala_version}! \
      clean \
      coverage \
      test \
      coverageReport \
      coverageAggregate \
      package
    sbt \
      -J-XX:MaxMetaspaceSize=1024m \
      -J-Xmx2048m \
      ++${scala_version}! \
      coveralls
  fi

  echo "============================================"
  echo "Building projects: Done"
  echo "============================================"
fi
