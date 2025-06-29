// API configuration and utility functions

export const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || '/api/v1';

export interface ApiError {
  message: string;
  status?: number;
}

export interface ClassDTO {
  id: number;
  seriesId: number;
  name: string;
  description: string;
}

export interface ClassesResponseDTO {
  eventId: number;
  eventName: string;
  classes: ClassDTO[];
}

export interface ManufacturerDTO {
  id: number;
  name: string;
  country: string;
  description: string | null;
}

export interface CarModelDTO {
  id: number;
  manufacturerId: number;
  name: string;
  fullName: string;
  yearModel: number;
  description: string;
  manufacturer: ManufacturerDTO;
}

// Updated response structure for the cars endpoint - now returns only car models
export interface CarModelsResponseDTO {
  eventId: number;
  eventName: string;
  classId: number;
  className: string;
  carModels: CarModelDTO[]; // Changed from cars to carModels
}

export interface SessionDTO {
  id: number;
  eventId: number;
  circuitId: number;
  name: string;
  type: string;
  startDatetime: string;
  durationSeconds: number;
  weatherAirTemp: number;
  weatherTrackTemp: number;
  weatherCondition: string;
  reportMessage: string;
  importTimestamp: string;
}

export interface SessionsResponseDTO {
  eventId: number;
  eventName: string;
  sessions: SessionDTO[];
}

export interface DriverDTO {
  driverId: number;
  firstName: string;
  lastName: string;
  fullName: string;
  nationality: string;
  hometown: string;
  licenseType: string;
  driverNumber: number;
}

export interface DriversResponseDTO {
  eventId: number;
  eventName: string;
  drivers: DriverDTO[];
}

export interface LapTimeDTO {
  lapId: number;
  lapNumber: number;
  lapTime: string;
  lapTimeSeconds: number;
  sessionElapsedSeconds: number;
  timestamp: string;
  averageSpeedKph: number;
}

export interface DriverLapTimesDTO {
  driverId: number;
  driverName: string;
  carNumber: string;
  teamName: string;
  carModel: string;
  className: string;
  lapTimes: LapTimeDTO[];
}

export interface LapTimesResponseDTO {
  eventId: number;
  sessionId: number;
  driverLapTimes: DriverLapTimesDTO[];
}

export interface ResultEntryDTO {
  id: number;
  sessionId: number;
  carEntryId: number;
  carNumber: string;
  tires: string;
  status: string;
  laps: number;
  totalTime: string;
  gapFirst: string;
  gapPrevious: string;
  flLapnum: number;
  flTime: string;
  flKph: number;
  position: number;
  carEntry: {
    carId: number;
    number: string;
    teamName: string;
    carModel: {
      id: number;
      name: string;
      fullName: string;
      yearModel: number;
      description: string;
    };
    tireSupplier: string;
    classId: number;
    teamId: number;
    drivers: {
      driverId: number;
      firstName: string;
      lastName: string;
      fullName: string;
      nationality: string;
      hometown: string;
      licenseType: string;
      driverNumber: number;
    }[];
  };
}

export interface ResultsResponseDTO {
  sessionId: number;
  results: ResultEntryDTO[];
}

export interface EventDTO {
  eventId: number;
  seriesId: number;
  name: string;
  year: number;
  startDate: string;
  endDate: string;
  description: string;
}

export type EventsResponseDTO = EventDTO[];

export async function fetchEventsForSeriesYear(
  seriesId: number,
  year: number,
  apiKey: string
): Promise<EventsResponseDTO> {
  return apiRequest<EventsResponseDTO>(`/events?seriesId=${seriesId}&year=${year}`, {
    headers: { 'X-API-Key': apiKey },
  });
}

export async function apiRequest<T>(
  endpoint: string,
  options: RequestInit = {}
): Promise<T> {
  const url = `${API_BASE_URL}${endpoint}`;
  
  try {
    const response = await fetch(url, {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error('API request failed:', error);
    throw error;
  }
}

// Helper function to build query parameters
export function buildQueryParams(params: Record<string, string | number | undefined>): string {
  const searchParams = new URLSearchParams();
  
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      searchParams.append(key, String(value));
    }
  });
  
  return searchParams.toString();
}

export interface ImportResponseDTO {
  importId: string;
  status: string;
  completionTime: number | null;
  error: string | null;
  url: string;
  importType: string;
  processType: string;
  sessionId: number;
} 