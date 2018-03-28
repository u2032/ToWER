#!/bin/bash

read -p "Enter version: " VERSION

mkdir build

echo Building standalone package...
mvn clean install -Ddeploy.version=$VERSION -Ddeploy.name=Tower -Ddeploy.artefactId=tower-core -Ddeploy.mainClass=land.tower.core.Application -Dconfig.file=config.properties -Pbundle,macos -Dbundle.native=image
cd target/package/bundles
zip -r Tower-$VERSION-standalone-macos.zip Tower.app
cd ../../..
mv -f target/package/bundles/Tower-$VERSION-standalone-macos.zip build

echo Building installer package...
mvn clean install -Ddeploy.version=$VERSION -Ddeploy.name=Tower -Ddeploy.artefactId=tower-core -Ddeploy.mainClass=land.tower.core.Application -Dconfig.file=config.properties -Pbundle,macos -Dbundle.native=pkg
mv -f target/package/bundles/Tower-$VERSION.pkg build/Tower-$VERSION-installer-macos.pkg
