#!/bin/bash
set -e

git pull

docker compose -f docker-compose.yml up -d --build db-prod be-prod

docker image prune -f
