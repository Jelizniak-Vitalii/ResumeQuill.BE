#!/bin/bash
set -e

git pull

docker compose -f docker-compose.dev.yml up -d --build

docker image prune -f
