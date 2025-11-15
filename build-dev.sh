#!/bin/bash
set -e

git pull

docker compose --env-file .env.dev -f docker-compose.yml up -d --build db-dev be-dev

docker image prune -f
