# Racing Stat Analysis API

This is the backend service for Racing Stat Analysis. It provides REST APIs for managing and analyzing racing statistics data.

## Getting Started

1. Install Java 21.
2. From the `api` directory, run:
   
   ```sh
   ./gradlew bootRun
   ```

The API will start on port 8080 by default.

## Features

- Manage racing series, events, teams, drivers, and lap times
- Perform lap time analysis and driver performance analysis
- Import racing data asynchronously
- RESTful API design with filtering and pagination support

## Main Endpoints

- `GET /api/v1/series` - Get all series
- `GET /api/v1/series/{seriesId}/{year}/events` - Get events for a series/year
- `GET /api/v1/series/events/{eventId}/teams` - Get teams for an event
- `GET /api/v1/series/events/{eventId}/classes` - Get classes for an event
- `GET /api/v1/series/events/{eventId}/classes/{classId}/cars` - Get car models for a class in an event
- `GET /api/v1/series/events/{eventId}/sessions` - Get sessions for an event
- `GET /api/v1/series/events/{eventId}/laptimeanalysis` - Get lap time analysis (with filters)
- `GET /api/v1/series/events/{eventId}/drivers` - Get drivers for an event (with filters)
- `GET /api/v1/series/events/{eventId}/session/{sessionId}/laptimes?driverIds=1,2,3` - Get lap times for drivers in a session
- `POST /api/v1/imports` - Start an async import job (requires `X-API-Key` header)
- `GET /api/v1/imports/status/{jobId}` - Get import job status

## Project Structure

```
src/main/java/com/arjunakankipati/racingstatanalysis/
├── controller/    # REST controllers
├── dto/           # Data transfer objects
├── model/         # Database models
├── repository/    # Data access layer
├── service/       # Business logic
└── ...
```

## Available Scripts

- `./gradlew bootRun` - Start the development server
- `./gradlew build` - Build the project
- `./gradlew test` - Run tests

## Technologies Used

- **Spring Boot** - Java backend framework
- **JPA/Hibernate** - ORM for database access
- **PostgreSQL** - Primary database
- **Gradle** - Build tool
- **Jooq** - Type-safe SQL queries

## Jooq Code Generation

Jooq is used for type-safe SQL query generation. The generated code is located in:

```
src/main/java/jooq/com/arjunakankipati/racingstatanalysis/jooq/
```

If you change the database schema (e.g., add or modify tables), you need to regenerate the Jooq classes. To do this, run:

```sh
./gradlew generateJooq
```

This will update the generated classes to match the current database schema.

If you need to recreate the DB locally, you can do so by running the script `reset_local_db.sh` in the scripts folder

## API Configuration

- The API runs on port 8080 by default.
- Set the `api.key` property in `application.properties` for import endpoints authentication.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License. 