# Racing Stat Analysis API: Detailed Request and Response Specifications

This document provides exact details about the requests and responses for each API endpoint in the Racing Stat Analysis application.

## Base URL

All API endpoints are prefixed with: `/api/v1`

## Series Controller

### Get All Series

**Request:**
- **URL**: `/series`
- **Method**: `GET`
- **Parameters**: None

**Response:**
- **Content-Type**: `application/json`
- **Status Code**: 200 OK
- **Body**: Array of `SeriesResponseDTO`
```json
[
  {
    "id": 1,
    "name": "IMSA WeatherTech SportsCar Championship",
    "eventCount": 12
  },
  {
    "id": 2,
    "name": "FIA World Endurance Championship",
    "eventCount": 8
  }
]
```

### Get Years for Series

**Request:**
- **URL**: `/series/{seriesId}/years`
- **Method**: `GET`
- **Path Parameters**:
  - `seriesId` (Long) - ID of the series

**Response:**
- **Content-Type**: `application/json`
- **Status Code**: 200 OK
- **Body**: `YearsResponseDTO`
```json
{
  "seriesId": 1,
  "seriesName": "IMSA WeatherTech SportsCar Championship",
  "years": [2021, 2022, 2023]
}
```

### Get Events for Series by Year

**Request:**
- **URL**: `/series/{seriesId}/{year}/events`
- **Method**: `GET`
- **Path Parameters**:
  - `seriesId` (Long) - ID of the series
  - `year` (Integer) - Year of the events

**Response:**
- **Content-Type**: `application/json`
- **Status Code**: 200 OK
- **Body**: Array of `EventsResponseDTO`
```json
[
  {
    "eventId": 1,
    "seriesId": 1,
    "name": "Rolex 24 At Daytona",
    "year": 2023,
    "startDate": "2023-01-26",
    "endDate": "2023-01-29",
    "description": "The 61st Rolex 24 At Daytona"
  },
  {
    "eventId": 2,
    "seriesId": 1,
    "name": "12 Hours of Sebring",
    "year": 2023,
    "startDate": "2023-03-15",
    "endDate": "2023-03-18",
    "description": "The 71st Mobil 1 Twelve Hours of Sebring"
  }
]
```

### Get Teams by Event ID

**Request:**
- **URL**: `/series/events/{eventId}/teams`
- **Method**: `GET`
- **Path Parameters**:
  - `eventId` (Long) - ID of the event

**Response:**
- **Content-Type**: `application/json`
- **Status Code**: 200 OK
- **Body**: `TeamsResponseDTO`
```json
{
  "eventId": 1,
  "eventName": "Rolex 24 At Daytona",
  "teams": [
    {
      "teamId": 1,
      "name": "Wayne Taylor Racing",
      "description": "Acura factory team",
      "cars": [
        {
          "carId": 1,
          "number": "10",
          "model": "Acura ARX-06",
          "tireSupplier": "Michelin",
          "classId": 1,
          "manufacturerId": 1,
          "drivers": [
            {
              "driverId": 1,
              "firstName": "Ricky",
              "lastName": "Taylor",
              "fullName": "Ricky Taylor",
              "nationality": "USA",
              "hometown": "Lake Forest, FL",
              "licenseType": "Platinum",
              "driverNumber": 10
            },
            {
              "driverId": 2,
              "firstName": "Filipe",
              "lastName": "Albuquerque",
              "fullName": "Filipe Albuquerque",
              "nationality": "Portugal",
              "hometown": "Coimbra",
              "licenseType": "Platinum",
              "driverNumber": 10
            }
          ]
        }
      ]
    }
  ]
}
```

### Get Lap Time Analysis for Event

**Request:**
- **URL**: `/series/events/{eventId}/laptimeanalysis`
- **Method**: `GET`
- **Path Parameters**:
  - `eventId` (Long) - ID of the event
- **Query Parameters**:
  - `percentage` (Integer, optional, default: 20) - Percentage of top lap times to include in the average calculation
  - `classId` (Long, optional) - Filter by class ID
  - `carId` (Long, optional) - Filter by car ID
  - `sessionId` (Long, optional) - Filter by session ID
  - `offset` (Integer, optional) - Pagination offset
  - `limit` (Integer, optional) - Pagination limit

**Response:**
- **Content-Type**: `application/json`
- **Status Code**: 200 OK
- **Body**: `LapTimeAnalysisResponseDTO`
```json
{
  "event": {
    "id": 1,
    "name": "Rolex 24 At Daytona",
    "year": 2023
  },
  "driverAnalyses": [
    {
      "driverId": 1,
      "driverName": "Ricky Taylor",
      "nationality": "USA",
      "carId": 1,
      "carNumber": "10",
      "carModel": "Acura ARX-06",
      "teamId": 1,
      "teamName": "Wayne Taylor Racing",
      "classId": 1,
      "className": "GTP",
      "averageLapTime": "1:34.567",
      "fastestLapTime": "1:33.123",
      "medianLapTime": "1:34.789",
      "totalLapCount": 120
    },
    {
      "driverId": 2,
      "driverName": "Filipe Albuquerque",
      "nationality": "Portugal",
      "carId": 1,
      "carNumber": "10",
      "carModel": "Acura ARX-06",
      "teamId": 1,
      "teamName": "Wayne Taylor Racing",
      "classId": 1,
      "className": "GTP",
      "averageLapTime": "1:34.789",
      "fastestLapTime": "1:33.456",
      "medianLapTime": "1:34.901",
      "totalLapCount": 115
    }
  ],
  "overallAnalysis": {
    "averageLapTime": "1:35.678",
    "fastestLapTime": "1:33.123",
    "medianLapTime": "1:35.456",
    "totalLapCount": 2500,
    "eventId": 1
  }
}
```

## Data Types

### SeriesResponseDTO
```json
{
  "id": 1,
  "name": "IMSA WeatherTech SportsCar Championship",
  "eventCount": 12
}
```

### YearsResponseDTO
```json
{
  "seriesId": 1,
  "seriesName": "IMSA WeatherTech SportsCar Championship",
  "years": [2021, 2022, 2023]
}
```

### EventsResponseDTO
```json
{
  "eventId": 1,
  "seriesId": 1,
  "name": "Rolex 24 At Daytona",
  "year": 2023,
  "startDate": "2023-01-26",
  "endDate": "2023-01-29",
  "description": "The 61st Rolex 24 At Daytona"
}
```

### TeamsResponseDTO
```json
{
  "eventId": 1,
  "eventName": "Rolex 24 At Daytona",
  "teams": [
    {
      "teamId": 1,
      "name": "Wayne Taylor Racing",
      "description": "Acura factory team",
      "cars": [
        {
          "carId": 1,
          "number": "10",
          "model": "Acura ARX-06",
          "tireSupplier": "Michelin",
          "classId": 1,
          "manufacturerId": 1,
          "drivers": [
            {
              "driverId": 1,
              "firstName": "Ricky",
              "lastName": "Taylor",
              "fullName": "Ricky Taylor",
              "nationality": "USA",
              "hometown": "Lake Forest, FL",
              "licenseType": "Platinum",
              "driverNumber": 10
            }
          ]
        }
      ]
    }
  ]
}
```

### DriverLapTimeAnalysisDTO
```json
{
  "driverId": 1,
  "driverName": "Ricky Taylor",
  "nationality": "USA",
  "carId": 1,
  "carNumber": "10",
  "carModel": "Acura ARX-06",
  "teamId": 1,
  "teamName": "Wayne Taylor Racing",
  "classId": 1,
  "className": "GTP",
  "averageLapTime": "1:34.567",
  "fastestLapTime": "1:33.123",
  "medianLapTime": "1:34.789",
  "totalLapCount": 120
}
```

### LapTimeAnalysisDTO
```json
{
  "averageLapTime": "1:35.678",
  "fastestLapTime": "1:33.123",
  "medianLapTime": "1:35.456",
  "totalLapCount": 2500,
  "eventId": 1
}
```

### LapTimeAnalysisResponseDTO
```json
{
  "event": {
    "id": 1,
    "name": "Rolex 24 At Daytona",
    "year": 2023
  },
  "driverAnalyses": [
    {
      "driverId": 1,
      "driverName": "Ricky Taylor",
      "nationality": "USA",
      "carId": 1,
      "carNumber": "10",
      "carModel": "Acura ARX-06",
      "teamId": 1,
      "teamName": "Wayne Taylor Racing",
      "classId": 1,
      "className": "GTP",
      "averageLapTime": "1:34.567",
      "fastestLapTime": "1:33.123",
      "medianLapTime": "1:34.789",
      "totalLapCount": 120
    }
  ],
  "overallAnalysis": {
    "averageLapTime": "1:35.678",
    "fastestLapTime": "1:33.123",
    "medianLapTime": "1:35.456",
    "totalLapCount": 2500,
    "eventId": 1
  }
}
```

### TopLapsResponseDTO
```json
{
  "driver": {
    "id": 1,
    "name": "Ricky Taylor",
    "nationality": "USA"
  },
  "topLaps": [
    {
      "lapTime": "1:33.123",
      "averageSpeedKph": 210.5,
      "event": "Rolex 24 At Daytona",
      "year": 2023,
      "carNumber": "10",
      "team": "Wayne Taylor Racing",
      "driver": "Ricky Taylor",
      "timestamp": "2023-01-28T14:35:22"
    }
  ]
}
```

### ImportRequestDTO
```json
{
  "url": "https://example.com/timing-data.json"
}
```

### ImportResponseDTO
```json
{
  "importId": "imp-123456",
  "status": "PROCESSING",
  "completionTime": null
}
```