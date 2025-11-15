#!/bin/bash
set -e

docker compose --env-file .env.local -f docker-compose.local.yml up -d --build

docker image prune -f
