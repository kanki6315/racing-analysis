'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { useParams } from 'next/navigation';
import { apiRequest, ClassesResponseDTO, ManufacturerDTO } from '@/lib/api';
import Spinner from '@/app/components/Spinner';

interface Event {
  eventId: number;
  seriesId: number;
  name: string;
  year: number;
  startDate: string;
  endDate: string;
  description: string;
}

interface Team {
  teamId: number;
  name: string;
  description: string;
  cars: Car[];
}

interface Car {
  carId: number;
  number: string;
  carModel: {
    id: number;
    manufacturerId: number;
    name: string;
    fullName: string;
    yearModel: number | null;
    description: string | null;
    manufacturer: ManufacturerDTO | null;
  };
  tireSupplier: string;
  classId: number;
  teamId: number;
  drivers: Driver[];
}

interface Driver {
  driverId: number;
  firstName: string;
  lastName: string;
  fullName: string;
  nationality: string;
  hometown: string;
  licenseType: string;
  driverNumber: number;
}

interface TeamsResponse {
  eventId: number;
  eventName: string;
  teams: Team[];
}

export default function YearPage() {
  const params = useParams();
  const seriesId = params.seriesId as string;
  const year = params.year as string;
  
  const [events, setEvents] = useState<Event[]>([]);
  const [eventTeams, setEventTeams] = useState<Record<number, TeamsResponse>>({});
  const [eventClasses, setEventClasses] = useState<Record<number, ClassesResponseDTO>>({});
  const [loadingEvents, setLoadingEvents] = useState(true);
  const [loadingEventDetails, setLoadingEventDetails] = useState<Record<number, boolean>>({});
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchEvents = async () => {
      try {
        // Fetch events for the series and year
        const eventsData: Event[] = await apiRequest<Event[]>(`/events?seriesId=${seriesId}&year=${year}`);
        setEvents(eventsData);
        setLoadingEvents(false);

        // For each event, fetch teams and classes in parallel
        eventsData.forEach(async (event) => {
          setLoadingEventDetails(prev => ({ ...prev, [event.eventId]: true }));
          try {
            const [teamsResult, classesResult] = await Promise.all([
              apiRequest<TeamsResponse>(`/events/${event.eventId}/teams`),
              apiRequest<ClassesResponseDTO>(`/events/${event.eventId}/classes`)
            ]);
            setEventTeams(prev => ({ ...prev, [event.eventId]: teamsResult }));
            setEventClasses(prev => ({ ...prev, [event.eventId]: classesResult }));
          } catch (err) {
            console.error(`Failed to fetch data for event ${event.eventId}:`, err);
          } finally {
            setLoadingEventDetails(prev => ({ ...prev, [event.eventId]: false }));
          }
        });
      } catch (err) {
        setError(err instanceof Error ? err.message : 'An error occurred');
        setLoadingEvents(false);
      }
    };

    if (seriesId && year) {
      fetchEvents();
    }
  }, [seriesId, year]);

  // Helper function to get class name by classId
  const getClassName = (eventId: number, classId: number): string => {
    const classes = eventClasses[eventId];
    if (!classes) return 'Unknown Class';
    
    const classItem = classes.classes.find(c => c.id === classId);
    return classItem ? classItem.name : 'Unknown Class';
  };

  // Helper function to get car model name safely
  const getCarModelName = (car: Car): string => {
    // Try different possible paths for car model
    if (car.carModel.name) {
      return car.carModel.name;
    }
    if (car.carModel.fullName) {
      return car.carModel.fullName;
    }
    return 'Unknown Model';
  };

  // Helper function to sort cars by class
  const getSortedCars = (eventId: number, teams: Team[]) => {
    const cars = teams.flatMap(team => 
      team.cars.map(car => ({
        ...car,
        team,
        className: getClassName(eventId, car.classId),
        carModelName: getCarModelName(car)
      }))
    );
    
    return cars.sort((a, b) => {
      // First sort by class name
      const classComparison = a.className.localeCompare(b.className);
      if (classComparison !== 0) return classComparison;
      
      // Then sort by car number within the same class
      return parseInt(a.number) - parseInt(b.number);
    });
  };

  if (loadingEvents) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-white">
        <Spinner />
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-xl text-red-600">Error: {error}</div>
      </div>
    );
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  };

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
          <h1 className="text-3xl font-bold text-gray-900">
            Events for {year}
          </h1>
        </div>

        <div className="grid gap-8">
          {(events.slice().sort((a, b) => new Date(a.startDate).getTime() - new Date(b.startDate).getTime())).map((event) => (
            <div
              key={event.eventId}
              className="bg-white rounded-lg shadow-md overflow-hidden"
            >
              {/* Event Header */}
              <div className="p-6 border-b border-gray-200">
                <div className="flex items-start justify-between">
                  <div>
                    <h2 className="text-2xl font-semibold text-gray-900 mb-2">
                      {event.name}
                    </h2>
                    <p className="text-gray-600 mb-2">{event.description}</p>
                    <div className="flex gap-4 text-sm text-gray-500">
                      <span>Start: {formatDate(event.startDate)}</span>
                      <span>End: {formatDate(event.endDate)}</span>
                    </div>
                  </div>
                  <div className="flex gap-2">
                    <Link
                      href={`/series/${seriesId}/${year}/results/${event.eventId}`}
                      className="inline-flex items-center px-4 py-2 bg-yellow-600 text-white text-sm font-medium rounded-md hover:bg-yellow-700 transition-colors"
                    >
                      View Results
                    </Link>
                    <Link
                      href={`/series/${seriesId}/${year}/analysis/${event.eventId}`}
                      className="inline-flex items-center px-4 py-2 bg-green-600 text-white text-sm font-medium rounded-md hover:bg-green-700 transition-colors"
                    >
                      View Analysis
                    </Link>
                    <Link
                      href={`/series/${seriesId}/${year}/laptimes/${event.eventId}`}
                      className="inline-flex items-center px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-md hover:bg-blue-700 transition-colors"
                    >
                      Lap Time Visualizer
                    </Link>
                  </div>
                </div>
              </div>

              {/* Teams Table */}
              {loadingEventDetails[event.eventId] ? (
                <div className="p-6 flex items-center justify-center min-h-[120px]">
                  <div className="flex flex-col items-center">
                    <div className="w-8 h-8 border-4 border-blue-200 border-t-blue-600 rounded-full animate-spin mb-2"></div>
                    <div className="text-gray-500">Loading teams and classes...</div>
                  </div>
                </div>
              ) : eventTeams[event.eventId] ? (
                <div className="p-6">
                  <h3 className="text-lg font-medium text-gray-700 mb-4">
                    Participating Teams ({eventTeams[event.eventId].teams.reduce((sum, team) => sum + (team.cars?.length || 0), 0)} cars)
                    {(() => {
                      // Count cars per class
                      const classCounts: Record<string, number> = {};
                      eventTeams[event.eventId].teams.forEach(team => {
                        team.cars.forEach(car => {
                          const className = getClassName(event.eventId, car.classId);
                          classCounts[className] = (classCounts[className] || 0) + 1;
                        });
                      });
                      // Format as '16 GTP | 20 GTD Pro | 20 GTD' (alphabetical by class name)
                      const summary = Object.entries(classCounts)
                        .sort((a, b) => a[0].localeCompare(b[0]))
                        .map(([className, count]) => `${count} ${className}`)
                        .join(' | ');
                      return summary ? (
                        <span className="ml-2 text-gray-500 text-sm">{summary}</span>
                      ) : null;
                    })()}
                  </h3>
                  
                  <div className="overflow-x-auto">
                    <div className="inline-block min-w-full align-middle">
                      <div className="overflow-hidden border border-gray-200 rounded-lg">
                        <div className="max-h-80 overflow-y-auto">
                          <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-50 sticky top-0 z-10">
                              <tr>
                                <th className="px-2 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                  Class
                                </th>
                                <th className="px-2 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                  #
                                </th>
                                <th className="px-2 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                  Team Name
                                </th>
                                <th className="px-2 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                  Car Model
                                </th>
                                <th className="px-2 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                  Drivers
                                </th>
                                <th className="px-2 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                  Tire Supplier
                                </th>
                              </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                              {getSortedCars(event.eventId, eventTeams[event.eventId].teams).map((car) => (
                                <tr key={`${car.team.teamId}-${car.carId}`} className="hover:bg-gray-50">
                                  <td className="px-2 py-2 whitespace-nowrap">
                                    <div className="text-sm font-medium text-gray-900">
                                      {car.className}
                                    </div>
                                  </td>
                                  <td className="px-2 py-2 whitespace-nowrap">
                                    <div className="text-sm font-bold text-gray-900">
                                      #{car.number}
                                    </div>
                                  </td>
                                  <td className="px-2 py-2 whitespace-nowrap">
                                    <div>
                                      <div className="text-sm font-medium text-gray-900">
                                        {car.team.name}
                                      </div>
                                      {car.team.description && (
                                        <div className="text-sm text-gray-500">
                                          {car.team.description}
                                        </div>
                                      )}
                                    </div>
                                  </td>
                                  <td className="px-2 py-2 whitespace-nowrap">
                                    <div className="text-sm text-gray-900">
                                      {car.carModelName}
                                    </div>
                                  </td>
                                  <td className="px-2 py-2">
                                    <div className="text-sm text-gray-900">
                                      {car.drivers.map((driver) => (
                                        <div key={driver.driverId} className="mb-1">
                                          <span className="font-medium">{driver.fullName}</span>
                                          <span className="text-gray-500 ml-2">
                                            ({driver.nationality})
                                          </span>
                                        </div>
                                      ))}
                                    </div>
                                  </td>
                                  <td className="px-2 py-2 whitespace-nowrap">
                                    <div className="text-sm text-gray-900">
                                      {car.tireSupplier}
                                    </div>
                                  </td>
                                </tr>
                              ))}
                            </tbody>
                          </table>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              ) : (
                <div className="p-6">
                  <p className="text-gray-500 text-center">No team data available for this event</p>
                </div>
              )}
            </div>
          ))}
        </div>

        {events.length === 0 && (
          <div className="text-center py-12">
            <p className="text-gray-500 text-lg">No events found for this year</p>
          </div>
        )}
      </div>
    </div>
  );
} 