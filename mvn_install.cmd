echo install xmeta engine
cd ../x-meta/org.xmeta.engine/
call mvn install -D maven.javadoc.skip=true
pause

echo install core
cd ../../xworker_core/
call mvn install  -D maven.javadoc.skip=true
pause

echo install dataobject
cd ../xworker_dataobject/xworker_dataobject/
call mvn install -D maven.javadoc.skip=true
pause

echo install web
cd ../../xworker_web/xworker_web/
call mvn install -D maven.javadoc.skip=true
pause

echo install swt
cd ../../xworker_swt/
call mvn install -D maven.javadoc.skip=true
pause

echo install app
cd ../xworker_app/xworker_app/
call mvn install -D maven.javadoc.skip=true
pause

echo install function
cd ../../xworker_function/xworker_function/
call mvn install -D maven.javadoc.skip=true
pause

echo install ai
cd ../../xworker_ai/xworker_ai/
call mvn install -D maven.javadoc.skip=true
pause

echo install jfreechart
cd ../../xworker_jfreechart/xworker_jfreechart/
call mvn install -D maven.javadoc.skip=true
pause

echo install uiflow
cd ../../xworker_uiflow/xworker_uiflow/
call mvn install -D maven.javadoc.skip=true -Dgpg.skip
pause

echo package misc
cd ../../xworker_misc/xworker_misc/
call mvn package -D maven.javadoc.skip=true
pause

echo package explorer
cd ../../xworker_explorer/xworker_explorer/
call mvn package -D maven.javadoc.skip=true
pause