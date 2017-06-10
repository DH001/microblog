#!/usr/bin/env bash

# Build the latest containers
mvn clean
mvn install -P docker -DskipTests -DskipITs
