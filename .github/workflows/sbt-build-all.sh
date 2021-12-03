#!/bin/bash -e

set -x

if [ -z "$1" ]
  then
    echo "Missing parameters. Please enter the [Scala version]."
    echo "sbt-build-all.sh 2.13.5"
    exit 1
else
  : ${CURRENT_BRANCH_NAME:?"CURRENT_BRANCH_NAME is missing."}

  scala_version=$1
  sbt_params="$2"
  echo "============================================"
  echo "Build projects"
  echo "--------------------------------------------"
  echo ""
  export SOURCE_DATE_EPOCH=$(date +%s)
  echo "SOURCE_DATE_EPOCH=$SOURCE_DATE_EPOCH"

  if [[ "$CURRENT_BRANCH_NAME" == "main" || "$CURRENT_BRANCH_NAME" == "release" ]]
  then
#    sbt -J-Xmx2048m ++${scala_version}! -v clean; coverage; test; coverageReport; coverageAggregate
#    sbt -J-Xmx2048m ++${scala_version}! -v coveralls
#    sbt -J-Xmx2048m ++${scala_version}! -v clean; packagedArtifacts
    sbt \
      -J-XX:MaxMetaspaceSize=1024m \
      -J-Xmx2048m \
      ++${scala_version}! \
      -v "${sbt_params}" \
      clean \
      test \
      packagedArtifacts
  else
#    sbt -J-Xmx2048m ++${scala_version}! -v clean coverage test coverageReport coverageAggregate package
#    sbt -J-Xmx2048m ++${scala_version}! -v coveralls
    sbt \
      -J-XX:MaxMetaspaceSize=1024m \
      -J-Xmx2048m \
      ++${scala_version}! \
      -v "${sbt_params}" \
      clean \
      test \
      package
  fi


  echo "============================================"
  echo "Building projects: Done"
  echo "============================================"
fi
