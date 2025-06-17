#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

# Stop and remove the existing container if it's running
# The '|| true' prevents the script from exiting if the container doesn't exist
echo "Stopping and removing existing 'statsdb' container..."
docker stop statsdb || true
docker rm statsdb || true

# Remove the old PostgreSQL data directory to ensure a fresh start
echo "Removing old database files..."
rm -rf /tmp/my_pgdata

# Start a new PostgreSQL container in the background
echo "Starting a new PostgreSQL container..."
docker run --name statsdb \
  -d \
  -e POSTGRES_PASSWORD=stats \
  -v /tmp/my_pgdata:/var/lib/postgresql/data \
  -p 5432:5432 \
  postgres

# Wait for the PostgreSQL server inside the container to be ready for connections
echo "Waiting for PostgreSQL to start..."
until docker exec statsdb pg_isready -U postgres > /dev/null 2>&1; do
  echo -n "."
  sleep 1
done
echo "\nPostgreSQL started."
sleep 5

# Create the 'statsdb' database
echo "Creating the 'statsdb' database..."
PGPASSWORD=stats psql -h localhost -U postgres -c "CREATE DATABASE statsdb;"

echo "✅ Local development database has been reset successfully!"