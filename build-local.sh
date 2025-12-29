#!/bin/bash
set -e

docker compose -p resumequill-be-local --env-file .env.local -f docker-compose.yml -f docker-compose.local.yml up -d --build

docker image prune -f
