#!/bin/bash -e

set -x

if [ -z "$1" ]
  then
    echo "Missing parameters. Please enter the [Scala version]."
    echo "sbt-build-all.sh 2.13.6"
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

  test_task="coverage test coverageReport coverageAggregate"

  if [[ "$CURRENT_BRANCH_NAME" == "main" || "$CURRENT_BRANCH_NAME" == "release" ]]
  then
    sbt \
      ++${scala_version}! \
      -v "${sbt_params}" \
      clean \
      ${test_task} \
      packagedArtifacts
  else
    sbt \
      ++${scala_version}! \
      -v "${sbt_params}" \
      clean \
      ${test_task} \
      package
  fi


  echo "============================================"
  echo "Building projects: Done"
  echo "============================================"
fi
