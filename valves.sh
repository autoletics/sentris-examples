#!/usr/bin/env bash

if [ $# -eq 0 ]
  then
    echo "No explicit conf (tvalve, rvalve) specified."
fi

# set the location of the satoris/sentris conf
conf="-Djxinsight.home=conf/valves/$1"

# the instrumentation agent arguments
agent="-server -javaagent:lib/sentris-javaagent.jar -noverify"

# turns off the automatic filtering of recursive method calls
recursive="-Djxinsight.server.probes.ext.aspectj.probe.nonrecursive.enabled=false"

# the classpath to the maven built library
classpath="lib/sentris-examples.jar"

# the driver class name used to simulate (controlled) flow
driver="io.ctrlconf.sentris.examples.valves.Driver"

java ${conf} ${agent} ${recursive} -cp ${classpath} ${driver}

