#!/bin/bash

timestampTemplate="+%Y-%m-%dT%H:%M:%S:%z"
echo "[$(date $timestampTemplate)] Composing file 'docker-compose.yaml'"
docker-compose up -d
echo "[$(date $timestampTemplate)] Composed file 'docker-compose.yaml'"
