echo install core
cd ./xworker_core/
call mvn deploy

echo install dataobject
cd ../xworker_dataobject/xworker_dataobject/
call mvn deploy

echo install web
cd ../../xworker_web/xworker_web/
call mvn deploy

echo install swt
cd ../../xworker_swt/
call mvn deploy

echo install app
cd ../xworker_app/xworker_app/
call mvn deploy

echo install function
cd ../../xworker_function/xworker_function/
call mvn deploy

echo install ai
cd ../../xworker_ai/xworker_ai/
call mvn deploy

echo install jfreechart
cd ../../xworker_jfreechart/xworker_jfreechart/
call mvn deploy

echo install misc
cd ../../xworker_misc/xworker_misc/
call mvn deploy