#!/bin/bash
set -e

git pull

docker compose -f docker-compose.yml up -d --build

docker image prune -f
