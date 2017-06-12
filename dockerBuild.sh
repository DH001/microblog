#!/usr/bin/env bash

mvn clean
mvn install -P docker -DskipTests -DskipITs