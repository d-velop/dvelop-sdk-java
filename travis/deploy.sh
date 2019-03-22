#!/usr/bin/env bash

# fail on any error
set -e

if [ ! -z "$TRAVIS_TAG" ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    echo "building for tag $TRAVIS_TAG"

    openssl aes-256-cbc -K $encrypted_cf4983ae76a5_key -iv $encrypted_cf4983ae76a5_iv -in travis/signingkey.asc.enc -out travis/signingkey.asc -d
    gpg --fast-import travis/signingkey.asc

    mvn clean deploy --settings travis/settings.xml -DskipTests=true --batch-mode -Prelease

else
    echo "not a tagged build"
fi