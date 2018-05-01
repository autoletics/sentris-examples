
SETLOCAL

set CONF=-Djxinsight.home=conf/valves/%1%
set AGENT=-server -javaagent:lib/sentris-javaagent.jar -noverify
set RECURSIVE="-Djxinsight.server.probes.ext.aspectj.probe.nonrecursive.enabled=false"
set CLASSPATH="lib/sentris-examples.jar"
set DRIVER="io.ctrlconf.sentris.examples.valves.Driver"

java %CONF% %AGENT% %RECURSIVE% -cp %CLASSPATH% %DRIVER%

ENDLOCAL