#!/bin/sh
nohup java -XX:+HeapDumpOnOutOfMemoryError -jar target/api-1.0.0.jar --spring.profiles.active=prod > target/console.log 2>&1 &
