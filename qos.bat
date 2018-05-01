
SETLOCAL

set CONF=-Djxinsight.home=conf/qos/%1%
set AGENT=-server -javaagent:lib/sentris-javaagent.jar -noverify
set CLASSPATH="lib/sentris-examples.jar"
set DRIVER="io.ctrlconf.sentris.examples.qos.Driver"

java %CONF% %AGENT% -cp %CLASSPATH% %DRIVER%

ENDLOCAL