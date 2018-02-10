
set /p VERSION=Enter version:

mkdir build

echo Building standalone package...
call mvn clean install -Ddeploy.version=%VERSION% -Ddeploy.name=Tower -Ddeploy.artefactId=tower-core -Ddeploy.mainClass=land.tower.core.Application -Dconfig.file=config.properties -Pbundle,windows -Dbundle.native=image
cd target\package\bundles
7z a Tower-%VERSION%-standalone-windows.zip .\Tower
cd ..\..\..
move /Y target\package\bundles\Tower-%VERSION%-standalone-windows.zip build

echo Building installer package...
call mvn clean install -Ddeploy.version=%VERSION% -Ddeploy.name=Tower -Ddeploy.artefactId=tower-core -Ddeploy.mainClass=land.tower.core.Application -Dconfig.file=config.properties -Pbundle,windows -Dbundle.native=exe
move /Y target\package\bundles\Tower-%VERSION%.exe build\Tower-%VERSION%-installer-windows.exe