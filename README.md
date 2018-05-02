A set of example configurations used to demonstrate some of the features within the Sentris QoS and Adaptive Control Valve extensions to Satoris.

[Slides: Controlling Software with Thought - On Quality of Service (QoS) & Adaptive Control for Microservices link](https://medium.com/@autoletics/slides-controlling-software-with-thought-9dca3b5bcfad)

## Project Layout

### Configuration

The `conf` directory contains the configuration for each of the example configurations.

There are two group of configurations, each in a separate directory. 

The Quality of Service (QoS) configurations are in stored `conf/qos` and the Adaptive Control Valves in `conf/valves`.

In each sub-directory below a group directory there are two files. One file is used for restricting instrumentation and the other for configuring the metering extensions including control policies.

The `jxinsight.override.config` file has two sections. The first section configures the Sentris control metering extension. The second section configures the default Satoris metering extensions and the visualization employed.

The Sentris metering extensions, built on top of Satoris, are `qos`, `tvalve`, and `rvalve`. 

* `qos` extension brings network traffic control management capabilities right up into the application call flow/graph
* `tvalve` extension adaptively optimizes throughput by controlling the concurrent execution flowing through a valve mapped to one or more methods
* `rvalve` extension adaptively optimizes response times by controlling the concurrent execution flowing through a valve mapped to one or more methods   

There are many more extensions within Sentris but for now I've chosen to focus on these particular ones in helping developers and engineers better appreciate control theory.

### Driver Code

There are two `Driver` classes along with a `DriverOps` utility class. 

The `io.ctrlconf.sentris.examples.qos.Driver` class simulates 2 services (classifications) each running 3 threads (flows).

The goal(s) for the `conf/qos` is to prioritize and police the processing across services and threads
  
The `io.ctrlconf.sentris.examples.valves.Driver` class simulates 10 threads racing down (in terms of call stack depth) to a bottleneck point. 

The goal for the `conf/valves` configurations is to adaptively throttle the processing at the outermost call point before hitting the contention.

### Library

The `sentris-javaagent.jar` provided during the workshop should be copied into the `lib` directory. 

Before running the scripts `mvn install` should be calls to create the `sentris-examples.jar` used by the scripts below.

A pre-built version of the application archive will also be made available during the workshop if need be.  

## How To Run

The examples can be run using the scripts, `qos.[sh|bat]` and `valves.[sh|bat]`, or via `mvn`.

### Prerequisites

* Java 8 (or later) runtime for running the examples including the rendering of the visualizations
* Maven for rebuilding the examples (but not when only the configuration is changed)
* The Sentris Java Agent library
 
### Scripts

The `qos.sh` will launch a 30 second running driver without any particular QoS policy installed.

To run with the `qos` driver with a particular configuration example execute: `qos.sh [example]`. For example:

`./qos.sh resource-capacity`

This single script argument maps to a directory under `conf/qos/`.

The order as per the above slides is:
`resource-capacity`
`service-capacity`
`min-reserve`
`priority-queue`
`rate-limit`
`reserve-pool`
`resource-barrier`
`reserve-lanes`

Likewise the `valves.sh` script allows for the following argument values to be passed:
`tvalve`
`rvalve`

Not passing an argument will effectively result in the driver running without any control.
 
 ### Maven
 
Before running execute `mvn clean install` 

To run the `qos` driver without any control employed execute:

`mvn exec:exec@qos`

To run the driver with a particular `qos` configuration execute:

`mvn exec:exec@qos -Dexample.conf=[example]`

For example:

`mvn exec:exec@qos -Dexample.conf=resource-capacity`

Alternatively execute:

`mvn exec:exec@qos -P resource-capacity`

To run the `valves` example without any control employed execute:

`mvn exec:exec@valves`

To run the driver with a particular `valves` configuration execute:

`mvn exec:exec@valves -Dexample.conf=[example]`

For example: 

`mvn exec:exec@valves -Dexample.conf=tvalve`

Alternatively execute:

`mvn exec:exec@valves -P tvalve`


 




 


