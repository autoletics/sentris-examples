#!/usr/bin/env bash

if [ $# -eq 0 ]
  then
    echo "No explicit conf (resource-capacity, service-capacity, min-reserve, priority-queue, rate-limit, reserve-pool, resource-barrier, reserve-lanes) specified."
fi

# set the location of the satoris/sentris conf
conf="-Djxinsight.home=conf/qos/$1"

# the instrumentation agent arguments
agent="-server -javaagent:lib/sentris-javaagent.jar -noverify"

# the classpath to the maven built library
classpath="lib/sentris-examples.jar"

# the driver class name used to simulate (controlled) flow
driver="io.ctrlconf.sentris.examples.qos.Driver"

java ${conf} ${agent} -cp ${classpath} ${driver}

