#!/bin/bash
set -e

git pull

for v in pgdata_prod grafana_data_prod loki_data_prod prometheus_data_prod; do
  docker volume inspect "$v" >/dev/null 2>&1 || docker volume create "$v"
done

docker compose \
  -p resumequill-be-prod \
  --env-file .env \
  -f docker-compose.base.yml \
  -f docker-compose.prod.yml \
  up -d --build

docker image prune -f --filter "until=168h"
