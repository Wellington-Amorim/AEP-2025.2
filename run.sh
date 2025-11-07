#!/usr/bin/env bash
# run.sh - build (if needed) and run the shaded JAR on Linux/macOS
set -euo pipefail

DB_URL="${DB_URL:-jdbc:mysql://localhost:3306/doacoes?createDatabaseIfNotExist=true&serverTimezone=UTC}"
DB_USER="${DB_USER:-root}"
DB_PASSWORD="${DB_PASSWORD:-}"
JAR="target/AEP-2025.2-1.0-SNAPSHOT-shaded.jar"

if [ ! -f "$JAR" ]; then
  echo "JAR not found: $JAR"
  echo "Building project (this may take a while)..."
  mvn -q -DskipTests package
fi

echo "Running application..."
java -Dfile.encoding=UTF-8 -Ddb.url="$DB_URL" -Ddb.username="$DB_USER" -Ddb.password="$DB_PASSWORD" -jar "$JAR"
