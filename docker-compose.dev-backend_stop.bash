#!/usr/bin/env bash
docker-compose -f docker-compose.dev-backend.yml down --rmi local -v
