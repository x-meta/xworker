echo install xmeta engine
cd ../x-meta/org.xmeta.engine/
call mvn install -D maven.javadoc.skip=true -Dgpg.skip

echo install core
cd ../../xworker/xworker_core/
call mvn install  -D maven.javadoc.skip=true  -Dgpg.skip

echo install dataobject
cd ../xworker_dataobject/
call mvn install -D maven.javadoc.skip=true  -Dgpg.skip

echo install web
cd ../xworker_web/
call mvn install -D maven.javadoc.skip=true -Dgpg.skip

echo install swt
cd ../xworker_swt/
call mvn install -D maven.javadoc.skip=true -Dgpg.skip

echo install app
cd ../xworker_app/
call mvn install -D maven.javadoc.skip=true -Dgpg.skip

echo install function
cd ../xworker_function/
call mvn install -D maven.javadoc.skip=true -Dgpg.skip

echo install ai
cd ../xworker_ai/
call mvn install -D maven.javadoc.skip=true -Dgpg.skip

echo install jfreechart
cd ../xworker_jfreechart/
call mvn install -D maven.javadoc.skip=true -Dgpg.skip

echo install uiflow
cd ../xworker_uiflow/
call mvn install -D maven.javadoc.skip=true -Dgpg.skip

echo install misc
cd ../xworker_misc/
call mvn install -D maven.javadoc.skip=true -Dgpg.skip

echo install libgdx
cd ../xworker_libgdx/
call mvn install -D maven.javadoc.skip=true -Dgpg.skip

echo package explorer
cd ../xworker_explorer/
call mvn package -D maven.javadoc.skip=true -Dgpg.skip
pause