#!/bin/sh
nohup java -XX:+HeapDumpOnOutOfMemoryError -jar target/api-1.0.0.jar > target/console.log 2>&1 &
