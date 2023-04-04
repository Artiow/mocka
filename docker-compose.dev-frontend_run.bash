#!/usr/bin/env bash

function get_exited() {
  docker ps -a -f "name=$1" -f "status=exited" --format "{{.ID}}"
}

function get_exited_by_code() {
  docker ps -a -f "name=$1" -f "exited=$2" --format "{{.ID}}"
}

function remove_on_successfully_exit() {
    # wait for container exit
    while [ -z "$(get_exited "$1")" ]; do sleep 0.25; done

    if [ -n "$(get_exited_by_code "$1" 1)" ]; then
      # try to restart container if it exit with code 1
      docker-compose -f docker-compose.dev-frontend.yml restart "$1"
      # wait for container again
      while [ -z "$(get_exited "$1")" ]; do sleep 0.25; done
    fi

    if [ -n "$(get_exited_by_code "$1" 0)" ]; then
      # remove stopped container if it exit with code 0
      docker-compose -f docker-compose.dev-frontend.yml rm -fv "$1"
    fi
}

# up containers
docker-compose -f docker-compose.dev-frontend.yml up -d
# wait for mongo-initializer container exit and remove it if exit code is 0
remove_on_successfully_exit mongo-initializer
# restart mongo-express
docker-compose -f docker-compose.dev-frontend.yml restart mongo-express
