#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

echo "Generating JAR..."
cd api
./gradlew bootJar

# Find the most recent JAR file in api/build/libs and rename it
echo "Finding and renaming the most recent JAR file..."
cd build/libs
latest_jar=$(ls -t *.jar | grep -v -- "-plain.jar" | head -n1)
if [ -n "$latest_jar" ]; then
    mv "$latest_jar" "racing-stat-analysis-latest.jar"
    echo "✅ Renamed $latest_jar to racing-stat-analysis-latest.jar"
else
    echo "❌ No JAR files found in api/build/libs"
    exit 1
fi
cd ../../..

