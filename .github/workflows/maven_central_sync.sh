#!/bin/bash -e

if [ "${GITHUB_TAG:-}" != "" ]; then

  : ${BINTRAY_USER:?"BINTRAY_USER is missing."}
  : ${BINTRAY_PASS:?"BINTRAY_PASS is missing."}

  PROJECT_VERSION="${GITHUB_TAG#v}"
  BINTRAY_SUBJECT=${BINTRAY_SUBJECT:-kevinlee}
  BINTRAY_REPO=${BINTRAY_REPO:-maven}

  echo "PROJECT_VERSION: $PROJECT_VERSION"
  echo "BINTRAY_SUBJECT: $BINTRAY_SUBJECT"
  echo "   BINTRAY_REPO: $BINTRAY_REPO"
  BINTRAY_PACKAGES="just-semver"
  for bintray_package in $BINTRAY_PACKAGES
  do
    echo ""
    echo "bintray_package: $bintray_package - Sync to Maven Central..."
    curl \
      --user "$BINTRAY_USER:$BINTRAY_PASS" \
      -X POST \
      -H "Content-Type: application/json" \
      "https://api.bintray.com/maven_central_sync/$BINTRAY_SUBJECT/$BINTRAY_REPO/$bintray_package/versions/$PROJECT_VERSION"

    sleep 10s
  done
else
  echo "It's not a tag release so 'Sync to Maven Central' has been skipped."
fi
