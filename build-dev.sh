#!/bin/bash
set -e

git pull

docker compose \
  -p resumequill-be-dev \
  --env-file .env.dev \
  -f docker-compose.base.yml \
  -f docker-compose.dev.yml \
  up -d --build

docker image prune -f --filter "until=168h"
