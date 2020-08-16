#!/bin/bash -e

set -x

if [ -z "$1" ]
  then
    echo "Scala version is missing. Please enter the Scala version."
    echo "sbt-build.sh 2.13.3"
    exit 1
else
  scala_version=$1
  echo "============================================"
  echo "Build projects"
  echo "--------------------------------------------"
  echo ""
  : ${CURRENT_BRANCH_NAME:?"CURRENT_BRANCH_NAME is missing."}
  : ${CI_BRANCH:?"CI_BRANCH is missing."}
  if [[ "$CURRENT_BRANCH_NAME" == "main" || "$CURRENT_BRANCH_NAME" == "release" ]]
  then
    sbt -J-Xmx2048m "; ++ ${scala_version}!; clean; coverage; test; coverageReport; coverageAggregate"
    sbt -J-Xmx2048m "; ++ ${scala_version}!; coveralls"
    sbt -J-Xmx2048m "; ++ ${scala_version}!; clean; packagedArtifacts"
  else
    sbt -J-Xmx2048m "; ++ ${scala_version}!; clean; coverage; test; coverageReport; coverageAggregate; package"
    sbt -J-Xmx2048m "; ++ ${scala_version}!; coveralls"
  fi


  echo "============================================"
  echo "Building projects: Done"
  echo "============================================"
fi
