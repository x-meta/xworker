echo install xmeta engine
cd ../x-meta/org.xmeta.engine/
ping -n 5 127.1>nul
call mvn deploy

echo install core
cd ../../xworker/xworker_core/
ping -n 5 127.1>nul
call mvn deploy

cd ../xworker_dataobject/
echo install dataobject
ping -n 5 127.1>nul
call mvn deploy

cd ../xworker_web/
echo install web
ping -n 5 127.1>nul
call mvn deploy

cd ../xworker_swt/
echo install swt
ping -n 5 127.1>nul
call mvn deploy

cd ../xworker_app/
echo install app
ping -n 5 127.1>nul
call mvn deploy

cd ../xworker_function/
echo install function
ping -n 5 127.1>nul
call mvn deploy

cd ../xworker_ai/
echo install ai
ping -n 5 127.1>nul
call mvn deploy

cd ../xworker_jfreechart/
echo install jfreechart
ping -n 5 127.1>nul
call mvn deploy

cd ../xworker_misc/
echo install misc
ping -n 5 127.1>nul
call mvn deploy

cd ../xworker_bigdata/
echo install bigdata
ping -n 5 127.1>nul
call mvn deploy

