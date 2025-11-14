#!/bin/bash
set -e

git pull

docker compose -f docker-compose.prod.yml up -d --build

# опционально: если хочешь еще и override (локальные тайны)
# docker compose -f docker-compose.yml -f docker-compose.prod.yml -f docker-compose.override.yml up -d --build

docker image prune -f
