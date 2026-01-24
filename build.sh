#!/bin/bash
set -e

git pull

docker compose -p resumequill-be-prod up -d --build

docker image prune -f --filter "until=168h"
