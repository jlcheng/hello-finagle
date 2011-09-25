#!/bin/bash

# My default vmargs for Scala development
SCALA="-Xverify:none -XX:MaxPermSize=128m -Xms1G -Xmx1G -Xss2M -XX:+UseTLAB \
 -XX:+AggressiveOpts -XX:+UseFastAccessorMethods"

# High throughput, but could have long GC pauses
GC_STRATEGY="-XX:+UseParallelGC -XX:+UseParallelOldGC"

java $SCALA $GC_STRATEGY -jar `dirname $0`/sbt-launch.jar "$@"
