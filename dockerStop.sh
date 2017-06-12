#!/bin/bash

echo Stopping Docker...
DOCKER_CMD=docker-compose

${DOCKER_CMD} --version
if [[ $? !=  0 ]]; then
   DOCKER_CMD=/usr/local/bin/docker-compose;
   echo docker-compose is not in PATH, reverting to full path: ${DOCKER_CMD};
fi
${DOCKER_CMD} down
