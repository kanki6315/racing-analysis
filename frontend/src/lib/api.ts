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
  importUrl: string;
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
  isValid: boolean;
  isPersonalBest: boolean;
  isSessionBest: boolean;
  invalidationReason: string | null;
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