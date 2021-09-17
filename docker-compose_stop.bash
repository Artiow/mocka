#!/usr/bin/env bash
docker-compose down --volumes --rmi local
docker image prune --force --filter label=stage=build