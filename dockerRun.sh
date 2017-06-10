#!/bin/bash

echo Launching Docker...

DOCKER_CMD=docker-compose
${DOCKER_CMD} --version

if [[ $? !=  0 ]]; then
   DOCKER_CMD=/usr/local/bin/docker-compose;
   echo docker-compose is not in PATH, reverting to full path: ${DOCKER_CMD};
fi

${DOCKER_CMD} -f docker/docker-compose.yml -f docker/docker-compose.static-ports.yml -p microblog up -d
