'use client';

import { useState, useEffect, useCallback } from 'react';
import Link from 'next/link';
import { useParams } from 'next/navigation';
import { 
  apiRequest, 
  buildQueryParams, 
  ClassesResponseDTO, 
  CarModelsResponseDTO, 
  SessionsResponseDTO 
} from '@/lib/api';

interface DriverAnalysis {
  driverId: number;
  driverName: string;
  nationality: string;
  carId: number;
  carNumber: string;
  carModel: string;
  teamId: number;
  teamName: string;
  classId: number;
  className: string;
  averageLapTime: string;
  fastestLapTime: string;
  medianLapTime: string;
  totalLapCount: number;
}

interface OverallAnalysis {
  averageLapTime: string;
  fastestLapTime: string;
  medianLapTime: string;
  totalLapCount: number;
  eventId: number;
}

interface Event {
  id: number;
  name: string;
  year: number;
}

interface LapTimeAnalysisResponse {
  event: Event;
  driverAnalyses: DriverAnalysis[];
  overallAnalysis: OverallAnalysis;
}

export default function AnalysisPage() {
  const params = useParams();
  const eventId = params.eventId as string;
  
  const [analysisData, setAnalysisData] = useState<LapTimeAnalysisResponse | null>(null);
  const [classesData, setClassesData] = useState<ClassesResponseDTO | null>(null);
  const [carModelsData, setCarModelsData] = useState<CarModelsResponseDTO | null>(null);
  const [sessionsData, setSessionsData] = useState<SessionsResponseDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [loadingClasses, setLoadingClasses] = useState(true);
  const [loadingCarModels, setLoadingCarModels] = useState(false);
  const [loadingSessions, setLoadingSessions] = useState(true);
  const [classesError, setClassesError] = useState<string | null>(null);
  const [carModelsError, setCarModelsError] = useState<string | null>(null);
  const [sessionsError, setSessionsError] = useState<string | null>(null);
  
  // Filter states
  const [sessionId, setSessionId] = useState<string>('');
  const [carModelId, setCarModelId] = useState<string>('');
  const [classId, setClassId] = useState<string>('');
  const [percentage, setPercentage] = useState<string>('20');
  
  // Pagination states
  const [currentPage, setCurrentPage] = useState(0);
  const [hasMoreData, setHasMoreData] = useState(true);
  const limit = 50;

  // Fetch classes for the event
  const fetchClasses = useCallback(async () => {
    try {
      setLoadingClasses(true);
      setClassesError(null);
      const data: ClassesResponseDTO = await apiRequest<ClassesResponseDTO>(`/series/events/${eventId}/classes`);
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
      const data: CarModelsResponseDTO = await apiRequest<CarModelsResponseDTO>(`/series/events/${eventId}/classes/${selectedClassId}/cars`);
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
      const data: SessionsResponseDTO = await apiRequest<SessionsResponseDTO>(`/series/events/${eventId}/sessions`);
      setSessionsData(data);
      setSessionId(''); // Default to "none"
    } catch (err) {
      setSessionsError(err instanceof Error ? err.message : 'Failed to fetch sessions');
      console.error('Failed to fetch sessions:', err);
    } finally {
      setLoadingSessions(false);
    }
  }, [eventId]);

  const fetchAnalysisData = useCallback(async (page: number = 0, resetData: boolean = false) => {
    try {
      const queryParams = buildQueryParams({
        limit: limit,
        offset: page * limit,
        percentage: percentage || '20',
        sessionId: sessionId || undefined,
        carId: carModelId || undefined, // Still use 'carId' parameter name for API
        classId: classId || undefined
      });

      const endpoint = `/series/events/${eventId}/laptimeanalysis${queryParams ? `?${queryParams}` : ''}`;
      const data: LapTimeAnalysisResponse = await apiRequest<LapTimeAnalysisResponse>(endpoint);
      
      if (resetData) {
        setAnalysisData(data);
        setCurrentPage(0);
      } else {
        setAnalysisData(prev => prev ? {
          ...prev,
          driverAnalyses: [...prev.driverAnalyses, ...data.driverAnalyses],
          overallAnalysis: data.overallAnalysis
        } : data);
      }

      // Check if we have more data
      setHasMoreData(data.driverAnalyses.length === limit);
    } catch (err) {
      console.error('Failed to fetch analysis data:', err);
    }
  }, [eventId, sessionId, carModelId, classId, percentage, limit]);

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

  // Fetch analysis data when filters change
  useEffect(() => {
    if (eventId && classId && !loadingClasses && !loadingCarModels && !loadingSessions) {
      setLoading(true);
      fetchAnalysisData(0, true).finally(() => setLoading(false));
    }
  }, [eventId, classId, sessionId, carModelId, percentage, loadingClasses, loadingCarModels, loadingSessions, fetchAnalysisData]);

  const handleFilterChange = () => {
    setLoading(true);
    fetchAnalysisData(0, true).finally(() => setLoading(false));
  };

  const handleNextPage = () => {
    const nextPage = currentPage + 1;
    setCurrentPage(nextPage);
    fetchAnalysisData(nextPage, false);
  };

  const handlePreviousPage = () => {
    if (currentPage > 0) {
      const prevPage = currentPage - 1;
      setCurrentPage(prevPage);
      // For simplicity, we'll refetch from the beginning
      // In a real app, you might want to cache previous pages
      fetchAnalysisData(0, true);
    }
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

  if (loadingClasses || loadingSessions) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-xl">Loading event data...</div>
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

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="mb-8">
          <Link
            href="/"
            className="inline-flex items-center text-blue-600 hover:text-blue-800 mb-4"
          >
            ← Back to Series
          </Link>
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            Lap Time Analysis
          </h1>
          <p className="text-lg text-gray-600">
            {classesData.eventName}
          </p>
        </div>

        {/* Filters */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-8">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Filters</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Session
              </label>
              <select
                value={sessionId}
                onChange={(e) => setSessionId(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-900 bg-white"
              >
                <option value="" className="text-gray-900">All Sessions</option>
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
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Percentage
              </label>
              <input
                type="number"
                value={percentage}
                onChange={(e) => setPercentage(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="20"
                min="1"
                max="100"
              />
            </div>
          </div>
          <div className="mt-4">
            <button
              onClick={handleFilterChange}
              disabled={loading}
              className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? 'Loading...' : 'Apply Filters'}
            </button>
          </div>
        </div>

        {/* Overall Analysis */}
        {analysisData && (
          <div className="bg-white rounded-lg shadow-md p-6 mb-8">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">Overall Analysis</h2>
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
              <div className="text-center">
                <div className="text-2xl font-bold text-blue-600">
                  {analysisData.overallAnalysis.averageLapTime}
                </div>
                <div className="text-sm text-gray-600">Average Lap Time</div>
              </div>
              <div className="text-center">
                <div className="text-2xl font-bold text-green-600">
                  {analysisData.overallAnalysis.fastestLapTime}
                </div>
                <div className="text-sm text-gray-600">Fastest Lap Time</div>
              </div>
              <div className="text-center">
                <div className="text-2xl font-bold text-purple-600">
                  {analysisData.overallAnalysis.medianLapTime}
                </div>
                <div className="text-sm text-gray-600">Median Lap Time</div>
              </div>
              <div className="text-center">
                <div className="text-2xl font-bold text-orange-600">
                  {analysisData.overallAnalysis.totalLapCount.toLocaleString()}
                </div>
                <div className="text-sm text-gray-600">Total Laps</div>
              </div>
            </div>
          </div>
        )}

        {/* Driver Analysis Table */}
        {analysisData && (
          <div className="bg-white rounded-lg shadow-md overflow-hidden">
            <div className="px-6 py-4 border-b border-gray-200">
              <h2 className="text-xl font-semibold text-gray-900">Driver Analysis</h2>
            </div>
            
            {loading && analysisData.driverAnalyses.length > 0 ? (
              <div className="p-6 text-center">
                <div className="text-gray-600">Loading more data...</div>
              </div>
            ) : (
              <>
                <div className="overflow-x-auto">
                  <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-50">
                      <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Rank
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Driver
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Car
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Team
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Class
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Average Lap
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Fastest Lap
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Median Lap
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Total Laps
                        </th>
                      </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                      {analysisData.driverAnalyses.map((driver, index) => (
                        <tr key={`${driver.driverId}-${index}`} className="hover:bg-gray-50">
                          <td className="px-6 py-4 whitespace-nowrap">
                            <div className="text-sm font-bold text-gray-900">
                              #{index + 1}
                            </div>
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap">
                            <div>
                              <div className="text-sm font-medium text-gray-900">
                                {driver.driverName}
                              </div>
                              <div className="text-sm text-gray-500">
                                {driver.nationality}
                              </div>
                            </div>
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap">
                            <div>
                              <div className="text-sm font-medium text-gray-900">
                                #{driver.carNumber}
                              </div>
                              <div className="text-sm text-gray-500">
                                {driver.carModel}
                              </div>
                            </div>
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            {driver.teamName}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            {driver.className}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            {driver.averageLapTime}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-green-600 font-medium">
                            {driver.fastestLapTime}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            {driver.medianLapTime}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            {driver.totalLapCount.toLocaleString()}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>

                {/* Pagination */}
                <div className="px-6 py-4 border-t border-gray-200 flex items-center justify-between">
                  <div className="text-sm text-gray-700">
                    Showing {analysisData.driverAnalyses.length} drivers
                  </div>
                  <div className="flex space-x-2">
                    <button
                      onClick={handlePreviousPage}
                      disabled={currentPage === 0 || loading}
                      className="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      Previous
                    </button>
                    <button
                      onClick={handleNextPage}
                      disabled={!hasMoreData || loading}
                      className="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      Next
                    </button>
                  </div>
                </div>
              </>
            )}
          </div>
        )}

        {analysisData && analysisData.driverAnalyses.length === 0 && !loading && (
          <div className="text-center py-12">
            <p className="text-gray-500 text-lg">No driver analysis data found for the selected filters</p>
          </div>
        )}

        {!analysisData && !loading && (
          <div className="text-center py-12">
            <p className="text-gray-500 text-lg">Select filters to view analysis data</p>
          </div>
        )}
      </div>
    </div>
  );
} 