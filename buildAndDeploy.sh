#!/bin/bash

VERSION=1.0
DEPLOY_DIR=/cicstart/jetty/webapps
GIT_DIR=/cicstart/deploy/cicstart
LIB_DIR=build/libs

gradle -b $GIT_DIR/build.gradle clean build

sudo -u jetty cp $GIT_DIR/auth/$LIB_DIR/auth-$VERSION.war $DEPLOY_DIR/auth.war
sudo -u jetty cp $GIT_DIR/catalogue/$LIB_DIR/catalogue-$VERSION.war $DEPLOY_DIR/catalogue.war
sudo -u jetty cp $GIT_DIR/file/$LIB_DIR/file-$VERSION.war $DEPLOY_DIR/file.war
sudo -u jetty cp $GIT_DIR/vfs/$LIB_DIR/vfs-$VERSION.war $DEPLOY_DIR/vfs.war

