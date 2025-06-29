#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

curl --location 'localhost:8080/api/v1/series' \
--header 'X-API-Key: your-secret-api-key-here' \
--header 'Content-Type: application/json' \
--data '{"name": "IMSA WeatherTech SportsCar Championship"}'

curl --location 'localhost:8080/api/v1/events' \
--header 'X-API-Key: your-secret-api-key-here' \
--header 'Content-Type: application/json' \
--data '{"name": "Rolex 24 at Daytona", "seriesId": 1, "startDate": "2025-01-25", "endDate": "2025-01-26", "year": "2025"}'

curl --location 'localhost:8080/api/v1/circuits' \
--header 'X-API-Key: your-secret-api-key-here' \
--header 'Content-Type: application/json' \
--data '{"name": "Daytona International Speedway", "lengthMeters": 5729.275, "country": "United States"}'

curl --location 'localhost:8080/api/v1/sessions' \
--header 'X-API-Key: your-secret-api-key-here' \
--header 'Content-Type: application/json' \
--data '{"eventId": 1, "circuitId": 1, "name": "Race", "type": "Race", "startDatetime": "2025-01-25T01:40:00", "durationSeconds": 86400}'