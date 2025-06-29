'use client';

import { useState, useEffect, useCallback } from 'react';
import Link from 'next/link';
import { useParams } from 'next/navigation';
import { 
  LineChart, 
  Line, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  Legend, 
  ResponsiveContainer 
} from 'recharts';
import { 
  apiRequest, 
  buildQueryParams, 
  ClassesResponseDTO, 
  CarModelsResponseDTO, 
  SessionsResponseDTO,
  DriversResponseDTO,
  LapTimesResponseDTO
} from '@/lib/api';
import Spinner from '@/app/components/Spinner';

const COLORS = [
  '#8884d8', '#82ca9d', '#ffc658', '#ff7300', '#ff0000',
  '#00ff00', '#0000ff', '#ffff00', '#ff00ff', '#00ffff'
];

export default function LapTimeVisualizerPage() {
  const params = useParams();
  const { seriesId, year, eventId } = params;
  
  const [classesData, setClassesData] = useState<ClassesResponseDTO | null>(null);
  const [carModelsData, setCarModelsData] = useState<CarModelsResponseDTO | null>(null);
  const [sessionsData, setSessionsData] = useState<SessionsResponseDTO | null>(null);
  const [driversData, setDriversData] = useState<DriversResponseDTO | null>(null);
  const [lapTimesData, setLapTimesData] = useState<LapTimesResponseDTO | null>(null);
  
  const [loadingClasses, setLoadingClasses] = useState(true);
  const [loadingCarModels, setLoadingCarModels] = useState(false);
  const [loadingSessions, setLoadingSessions] = useState(true);
  const [loadingDrivers, setLoadingDrivers] = useState(false);
  const [loadingLapTimes, setLoadingLapTimes] = useState(false);
  
  const [classesError, setClassesError] = useState<string | null>(null);
  const [carModelsError, setCarModelsError] = useState<string | null>(null);
  const [sessionsError, setSessionsError] = useState<string | null>(null);
  const [driversError, setDriversError] = useState<string | null>(null);
  const [lapTimesError, setLapTimesError] = useState<string | null>(null);
  
  // Filter states
  const [sessionId, setSessionId] = useState<string>('');
  const [carModelId, setCarModelId] = useState<string>('');
  const [classId, setClassId] = useState<string>('');
  const [selectedDrivers, setSelectedDrivers] = useState<number[]>([]);
  
  // Fetch classes for the event
  const fetchClasses = useCallback(async () => {
    try {
      setLoadingClasses(true);
      setClassesError(null);
      const data: ClassesResponseDTO = await apiRequest<ClassesResponseDTO>(`/events/${eventId}/classes`);
      setClassesData(data);
      
      // Set the default class to the one with the highest ID
      if (data.classes.length > 0) {
        const highestClass = data.classes.reduce((prev, current) => 
          (prev.id > current.id) ? prev : current
        );
        setClassId(highestClass.id.toString());
      }
    } catch (err) {
      setClassesError(err instanceof Error ? err.message : 'Failed to fetch classes');
      console.error('Failed to fetch classes:', err);
    } finally {
      setLoadingClasses(false);
    }
  }, [eventId]);

  // Fetch car models for the selected class
  const fetchCarModels = useCallback(async (selectedClassId: string) => {
    if (!selectedClassId) {
      setCarModelsData(null);
      setCarModelId('');
      return;
    }

    try {
      setLoadingCarModels(true);
      setCarModelsError(null);
      const data: CarModelsResponseDTO = await apiRequest<CarModelsResponseDTO>(`/events/${eventId}/classes/${selectedClassId}/cars`);
      setCarModelsData(data);
      setCarModelId(''); // Reset car model selection to "none"
    } catch (err) {
      setCarModelsError(err instanceof Error ? err.message : 'Failed to fetch car models');
      console.error('Failed to fetch car models:', err);
      setCarModelsData(null);
      setCarModelId('');
    } finally {
      setLoadingCarModels(false);
    }
  }, [eventId]);

  // Fetch sessions for the event
  const fetchSessions = useCallback(async () => {
    try {
      setLoadingSessions(true);
      setSessionsError(null);
      const data: SessionsResponseDTO = await apiRequest<SessionsResponseDTO>(`/events/${eventId}/sessions`);
      setSessionsData(data);
      
      // Auto-select the first session
      if (data.sessions.length > 0) {
        setSessionId(data.sessions[0].id.toString());
      } else {
        setSessionId(''); // Default to "none" if no sessions
      }
    } catch (err) {
      setSessionsError(err instanceof Error ? err.message : 'Failed to fetch sessions');
      console.error('Failed to fetch sessions:', err);
    } finally {
      setLoadingSessions(false);
    }
  }, [eventId]);

  // Fetch drivers for the event with optional filters
  const fetchDrivers = useCallback(async (selectedClassId: string, selectedCarModelId: string) => {
    try {
      setLoadingDrivers(true);
      setDriversError(null);
      
      const queryParams = buildQueryParams({
        classId: selectedClassId || undefined,
        carModelId: selectedCarModelId || undefined
      });

      const endpoint = `/events/${eventId}/drivers${queryParams ? `?${queryParams}` : ''}`;
      const data: DriversResponseDTO = await apiRequest<DriversResponseDTO>(endpoint);
      setDriversData(data);
      setSelectedDrivers([]); // Reset selected drivers
    } catch (err) {
      setDriversError(err instanceof Error ? err.message : 'Failed to fetch drivers');
      console.error('Failed to fetch drivers:', err);
      setDriversData(null);
      setSelectedDrivers([]);
    } finally {
      setLoadingDrivers(false);
    }
  }, [eventId]);

  // Fetch lap times for selected drivers and session
  const fetchLapTimes = useCallback(async () => {
    if (!sessionId || selectedDrivers.length === 0) {
      setLapTimesData(null);
      return;
    }

    try {
      setLoadingLapTimes(true);
      setLapTimesError(null);
      
      const driverIdsParam = selectedDrivers.join(',');
      const endpoint = `/events/${eventId}/session/${sessionId}/laptimes?driverIds=${driverIdsParam}`;
      const data: LapTimesResponseDTO = await apiRequest<LapTimesResponseDTO>(endpoint);
      setLapTimesData(data);
    } catch (err) {
      setLapTimesError(err instanceof Error ? err.message : 'Failed to fetch lap times');
      console.error('Failed to fetch lap times:', err);
      setLapTimesData(null);
    } finally {
      setLoadingLapTimes(false);
    }
  }, [eventId, sessionId, selectedDrivers]);

  // Fetch classes and sessions first
  useEffect(() => {
    if (eventId) {
      fetchClasses();
      fetchSessions();
    }
  }, [eventId, fetchClasses, fetchSessions]);

  // Fetch car models when class changes
  useEffect(() => {
    if (eventId && classId) {
      fetchCarModels(classId);
    } else {
      setCarModelsData(null);
      setCarModelId('');
    }
  }, [eventId, classId, fetchCarModels]);

  // Fetch drivers when class or car model changes
  useEffect(() => {
    if (eventId && !loadingClasses && !loadingCarModels) {
      fetchDrivers(classId, carModelId);
    }
  }, [eventId, classId, carModelId, loadingClasses, loadingCarModels, fetchDrivers]);

  // Fetch lap times when session or selected drivers change
  useEffect(() => {
    if (eventId && sessionId && selectedDrivers.length > 0) {
      fetchLapTimes();
    } else {
      setLapTimesData(null);
    }
  }, [eventId, sessionId, selectedDrivers, fetchLapTimes]);

  const handleDriverSelection = (driverId: number) => {
    setSelectedDrivers(prev => {
      if (prev.includes(driverId)) {
        return prev.filter(id => id !== driverId);
      } else {
        if (prev.length >= 10) {
          alert('Maximum 10 drivers can be selected. Please remove one selection before adding another.');
          return prev;
        }
        return [...prev, driverId];
      }
    });
  };

  // Helper function to format datetime
  const formatDateTime = (dateTime: string) => {
    return new Date(dateTime).toLocaleString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  // Prepare chart data
  const prepareChartData = () => {
    if (!lapTimesData) return { data: [], yDomain: [0, 100] };

    // Get all unique lap numbers
    const allLapNumbers = new Set<number>();
    const allLapTimes: number[] = [];
    
    lapTimesData.driverLapTimes.forEach(driver => {
      driver.lapTimes.forEach(lap => {
        allLapNumbers.add(lap.lapNumber);
        allLapTimes.push(lap.lapTimeSeconds);
      });
    });

    const sortedLapNumbers = Array.from(allLapNumbers).sort((a, b) => a - b);

    // Calculate dynamic Y-axis range (±2 seconds around the data)
    const minLapTime = Math.min(...allLapTimes);
    const maxLapTime = Math.max(...allLapTimes);
    const yMin = Math.max(0, minLapTime - 2); // Don't go below 0
    const yMax = maxLapTime + 2;
    const yDomain = [yMin, yMax];

    // Create data points for each lap number
    const data = sortedLapNumbers.map(lapNumber => {
      const dataPoint: Record<string, number> = { lapNumber };
      
      lapTimesData.driverLapTimes.forEach((driver) => {
        const lapTime = driver.lapTimes.find(lap => lap.lapNumber === lapNumber);
        if (lapTime) {
          dataPoint[`driver_${driver.driverId}`] = lapTime.lapTimeSeconds;
        }
      });
      
      return dataPoint;
    });

    return { data, yDomain };
  };

  if (loadingClasses || loadingSessions) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-white">
        <Spinner />
      </div>
    );
  }

  if (classesError || sessionsError) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-xl text-red-600">
          Error: {classesError || sessionsError}
        </div>
      </div>
    );
  }

  if (!classesData || classesData.classes.length === 0) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-xl">No classes found for this event</div>
      </div>
    );
  }

  const { data, yDomain } = prepareChartData();

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="mb-8">
          <Link
            href={`/series/${seriesId}/${year}`}
            className="inline-flex items-center text-blue-600 hover:text-blue-800 mb-4"
          >
            ← Back to Events
          </Link>
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            Lap Time Visualizer
          </h1>
          <p className="text-lg text-gray-600">
            {classesData.eventName}
          </p>
        </div>

        {/* Filters */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-8">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Filters</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Session *
              </label>
              <select
                value={sessionId}
                onChange={(e) => setSessionId(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-900 bg-white"
              >
                <option value="" className="text-gray-900">Select Session</option>
                {sessionsData?.sessions.map((session) => (
                  <option key={session.id} value={session.id} className="text-gray-900">
                    {session.name} ({session.type}) - {formatDateTime(session.startDatetime)}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Class
              </label>
              <select
                value={classId}
                onChange={(e) => setClassId(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-900 bg-white"
              >
                {classesData.classes.map((classItem) => (
                  <option key={classItem.id} value={classItem.id} className="text-gray-900">
                    {classItem.name} - {classItem.description}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Car Model
              </label>
              <select
                value={carModelId}
                onChange={(e) => setCarModelId(e.target.value)}
                disabled={!classId || loadingCarModels}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100 disabled:cursor-not-allowed text-gray-900 bg-white"
              >
                <option value="" className="text-gray-900">All Car Models</option>
                {carModelsData?.carModels.map((carModel) => (
                  <option key={carModel.id} value={carModel.id} className="text-gray-900">
                    {carModel.fullName}
                  </option>
                ))}
              </select>
              {loadingCarModels && (
                <div className="text-xs text-gray-500 mt-1">Loading car models...</div>
              )}
              {carModelsError && (
                <div className="text-xs text-red-500 mt-1">{carModelsError}</div>
              )}
            </div>
          </div>

          {/* Drivers Selection */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Drivers * (Select up to 10)
            </label>
            {loadingDrivers ? (
              <div className="text-sm text-gray-500">Loading drivers...</div>
            ) : driversError ? (
              <div className="text-sm text-red-500">{driversError}</div>
            ) : driversData ? (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-2 max-h-48 overflow-y-auto border border-gray-200 rounded-md p-3">
                {driversData.drivers.map((driver) => (
                  <label key={driver.driverId} className="flex items-center space-x-2 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={selectedDrivers.includes(driver.driverId)}
                      onChange={() => handleDriverSelection(driver.driverId)}
                      className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                    />
                    <span className="text-sm text-gray-900">
                      {driver.fullName} ({driver.nationality})
                    </span>
                  </label>
                ))}
              </div>
            ) : (
              <div className="text-sm text-gray-500">No drivers available</div>
            )}
            <div className="text-xs text-gray-500 mt-1">
              Selected: {selectedDrivers.length}/10 drivers
            </div>
          </div>
        </div>

        {/* Chart */}
        {lapTimesData && data.length > 0 && (
          <div className="bg-white rounded-lg shadow-md p-6 mb-8">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">Lap Time Chart</h2>
            <div className="h-96 mb-8">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={data}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis 
                    dataKey="lapNumber" 
                    label={{ value: 'Lap Number', position: 'insideBottom', offset: -10 }}
                    tick={{ fontSize: 12 }}
                    interval={5}
                  />
                  <YAxis 
                    label={{ value: 'Lap Time (seconds)', angle: -90, position: 'insideLeft' }}
                    domain={yDomain}
                  />
                  <Tooltip 
                    formatter={(value: number) => [`${value.toFixed(3)}s`, 'Lap Time']}
                    labelFormatter={(label) => `Lap ${label}`}
                  />
                  <Legend />
                  {lapTimesData.driverLapTimes.map((driver, index) => (
                    <Line
                      key={driver.driverId}
                      type="monotone"
                      dataKey={`driver_${driver.driverId}`}
                      stroke={COLORS[index % COLORS.length]}
                      strokeWidth={2}
                      dot={false}
                      name={`${driver.driverName} (#${driver.carNumber})`}
                      connectNulls={false}
                    />
                  ))}
                </LineChart>
              </ResponsiveContainer>
            </div>
          </div>
        )}

        {/* Error Messages */}
        {lapTimesError && (
          <div className="bg-red-50 border border-red-200 rounded-md p-4 mb-8">
            <div className="text-red-800">Error loading lap times: {lapTimesError}</div>
          </div>
        )}

        {/* Loading State */}
        {loadingLapTimes && (
          <div className="bg-white rounded-lg shadow-md p-6 mb-8">
            <div className="text-center">
              <div className="text-gray-600">Loading lap time data...</div>
            </div>
          </div>
        )}

        {/* No Data State */}
        {!lapTimesData && !loadingLapTimes && sessionId && selectedDrivers.length > 0 && (
          <div className="bg-white rounded-lg shadow-md p-6 mb-8">
            <div className="text-center">
              <div className="text-gray-600">No lap time data found for the selected criteria</div>
            </div>
          </div>
        )}

        {/* Instructions */}
        {!sessionId || selectedDrivers.length === 0 ? (
          <div className="bg-blue-50 border border-blue-200 rounded-md p-4">
            <div className="text-blue-800">
              <strong>Instructions:</strong> Please select a session and at least one driver to view lap time data.
            </div>
          </div>
        ) : null}
      </div>
    </div>
  );
} 