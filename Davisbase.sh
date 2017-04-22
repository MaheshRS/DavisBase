#!/usr/bin/env bash

find ./* -type f -path "./*/*.class" | xargs -n 1 rm
rm -rf ./output
mkdir output

# Compile Code.
export CLASSPATH=./output:./lib/json-simple.jar

#-Xlint:unchecked -Xlint:-deprecation
javac -classpath $CLASSPATH -d ./output ./*/*/*.java

java main.UserPrompt