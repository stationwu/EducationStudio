#!/bin/sh

git pull;

mvn clean package -DskipTests=true;

for f in target/*.jar; do
    java -jar "$f"
    break
done
