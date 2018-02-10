#!/bin/bash

read -p "Enter version: " VERSION

mkdir build

echo Building standalone package...
mvn clean install -Ddeploy.version=$VERSION -Ddeploy.name=Tower -Ddeploy.artefactId=tower-core -Ddeploy.mainClass=land.tower.core.Application -Dconfig.file=config.properties -Pbundle,linux -Dbundle.native=image
cd target/package/bundles
tar -zcvf Tower-$VERSION-standalone-linux.tar.gz ./Tower
cd ../../..
mv -f target/package/bundles/Tower-$VERSION-standalone-linux.tar.gz build
chmod -R +w target
rm -rf target

echo Building installer package...
mvn clean install -Ddeploy.version=$VERSION -Ddeploy.name=Tower -Ddeploy.artefactId=tower-core -Ddeploy.mainClass=land.tower.core.Application -Dconfig.file=config.properties -Pbundle,linux -Dbundle.native=deb
mv -f target/package/bundles/tower-$VERSION.deb build/tower-$VERSION-installer-linux.deb
chmod -R +w target
rm -rf target
