#!/bin/bash -e

set -x

echo "============================================"
echo "Build projects"
echo "--------------------------------------------"
echo ""
if [[ "$BRANCH_NAME" == "rc" ]]
then
  sbt -d -J-Xmx2048m clean +coverage +test +coverageReport +coverageAggregate
  sbt -d -J-Xmx2048m +packageBin +packageSrc +packageDoc
else
  sbt -d -J-Xmx2048m clean +coverage +test +coverageReport +coverageAggregate +package
fi

sbt -d -J-Xmx2048m +coveralls

echo "============================================"
echo "Building projects: Done"
echo "============================================"
