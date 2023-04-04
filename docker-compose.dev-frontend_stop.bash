#!/usr/bin/env bash
docker-compose -f docker-compose.dev-frontend.yml down --rmi local -v
