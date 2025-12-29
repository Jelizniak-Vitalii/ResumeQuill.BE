#!/bin/bash
set -e

git pull

docker compose -p resumequill-be-prod -f docker-compose.yml up -d --build

docker image prune -f
