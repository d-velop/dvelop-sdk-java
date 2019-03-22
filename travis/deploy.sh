#!/usr/bin/env bash

# fail on any error
set -e

if [ ! -z "$TRAVIS_TAG" ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    echo "building for tag $TRAVIS_TAG"

    openssl aes-256-cbc -K $encrypted_0e0e4536fec9_key -iv $encrypted_0e0e4536fec9_iv -in travis/signature.asc.enc -out travis/signature.asc -d
    gpg --fast-import travis/signature.asc

    mvn deploy --settings travis/settings.xml -DskipTests=true --batch-mode -Prelease

else
    echo "not a tagged build"
fi