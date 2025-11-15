#!/bin/bash
set -e

git pull

docker compose --env-file .env.dev -f docker-compose.dev.yml up -d --build

docker image prune -f
