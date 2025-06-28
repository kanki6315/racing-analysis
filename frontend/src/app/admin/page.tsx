"use client";

import { useEffect, useState, useMemo } from "react";
import { apiRequest, API_BASE_URL, fetchEventsForSeriesYear, EventDTO } from "../../lib/api";
import Spinner from "../components/Spinner";
import React from "react";
import CreateSessionModal from "../components/CreateSessionModal";
import CreateCircuitModal from '../components/CreateCircuitModal';

interface Series {
  id: number;
  name: string;
  eventCount: number;
  years: number[];
}

export default function AdminPage() {
  const [apiKey, setApiKey] = useState<string | null>(null);
  const [inputKey, setInputKey] = useState("");
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [series, setSeries] = useState<Series[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [eventsBySeries, setEventsBySeries] = useState<Record<number, EventDTO[]>>({});
  const [selectedYear, setSelectedYear] = useState<Record<number, number>>({});
  const [showNewSeriesModal, setShowNewSeriesModal] = useState(false);
  const [newSeriesName, setNewSeriesName] = useState("");
  const [modalError, setModalError] = useState<string | null>(null);
  const [modalSuccess, setModalSuccess] = useState(false);
  const [modalLoading, setModalLoading] = useState(false);
  const [createSessionModal, setCreateSessionModal] = React.useState<{eventId: number, eventName: string} | null>(null);
  const [showNewCircuitModal, setShowNewCircuitModal] = useState(false);

  // Check for apiKey in localStorage and validate
  useEffect(() => {
    const storedKey = localStorage.getItem("apiKey");
    if (storedKey) {
      validateKey(storedKey);
    }
  }, []);

  // Validate the API key
  const validateKey = async (key: string) => {
    setLoading(true);
    setError(null);
    try {
      const res = await fetch(`${API_BASE_URL}/auth/check`, {
        headers: { "X-API-Key": key },
      });
      if (res.status === 200) {
        setApiKey(key);
        setIsAuthenticated(true);
        localStorage.setItem("apiKey", key);
        fetchSeries(key);
      } else {
        setIsAuthenticated(false);
        setApiKey(null);
        setError("Invalid API key. Please try again.");
        localStorage.removeItem("apiKey");
      }
    } catch (e) {
      setError("Failed to validate API key.");
      setIsAuthenticated(false);
      setApiKey(null);
    } finally {
      setLoading(false);
    }
  };

  // Fetch all series
  const fetchSeries = async (key: string) => {
    setLoading(true);
    setError(null);
    try {
      const data = await apiRequest<Series[]>("/series", {
        headers: { "X-API-Key": key },
      });
      setSeries(data);
    } catch (e) {
      setError("Failed to fetch series data.");
    } finally {
      setLoading(false);
    }
  };

  // Fetch events for a series and year
  const loadEvents = async (seriesId: number, year: number, key: string) => {
    try {
      const events = await fetchEventsForSeriesYear(seriesId, year, key);
      setEventsBySeries(prev => ({ ...prev, [seriesId]: events }));
    } catch (e) {
      setEventsBySeries(prev => ({ ...prev, [seriesId]: [] }));
    }
  };

  // When series or apiKey changes, set default year and load events
  useEffect(() => {
    if (series.length > 0 && apiKey) {
      const newSelectedYear: Record<number, number> = {};
      series.forEach(s => {
        if (s.years && s.years.length > 0) {
          const mostRecent = Math.max(...s.years);
          newSelectedYear[s.id] = mostRecent;
          loadEvents(s.id, mostRecent, apiKey);
        }
      });
      setSelectedYear(newSelectedYear);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [series, apiKey]);

  // Handle year change for a series
  const handleYearChange = (seriesId: number, year: number) => {
    setSelectedYear(prev => ({ ...prev, [seriesId]: year }));
    if (apiKey) loadEvents(seriesId, year, apiKey);
  };

  // Handle API key form submit
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    validateKey(inputKey);
  };

  // Create new series
  const handleCreateSeries = async (e: React.FormEvent) => {
    e.preventDefault();
    setModalLoading(true);
    setModalError(null);
    try {
      const res = await fetch(`${API_BASE_URL}/series`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-API-Key": apiKey || "",
        },
        body: JSON.stringify({ name: newSeriesName }),
      });
      if (res.status === 409) {
        setModalError("Series already exists");
      } else if (!res.ok) {
        setModalError("Something went wrong");
      } else {
        setModalSuccess(true);
        setShowNewSeriesModal(false);
        setNewSeriesName("");
        // Refresh series list
        fetchSeries(apiKey!);
      }
    } catch (err) {
      setModalError("Something went wrong");
    } finally {
      setModalLoading(false);
    }
  };

  const handleOpenCreateSession = (eventId: number, eventName: string) => setCreateSessionModal({ eventId, eventName });
  const handleCloseCreateSession = () => setCreateSessionModal(null);

  if (!isAuthenticated) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="bg-white rounded-lg shadow-md p-8 w-full max-w-md">
          <h2 className="text-2xl font-bold text-gray-900 mb-4 text-center">Admin Login</h2>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label htmlFor="apiKey" className="block text-gray-700 font-medium mb-1">API Key</label>
              <input
                id="apiKey"
                type="password"
                value={inputKey}
                onChange={e => setInputKey(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                autoFocus
              />
            </div>
            <button
              type="submit"
              className="w-full py-2 px-4 bg-blue-600 text-white font-semibold rounded-md hover:bg-blue-700 transition-colors"
              disabled={loading}
            >
              {loading ? <Spinner /> : "Login"}
            </button>
          </form>
          {error && <div className="text-red-600 mt-4 text-center">{error}</div>}
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8 relative">
      {/* Blur overlay when modal is open */}
      {showNewSeriesModal && (
        <div className="fixed inset-0 z-40 backdrop-blur-sm pointer-events-none"></div>
      )}
      {showNewCircuitModal && (
        <div className="fixed inset-0 z-40 backdrop-blur-sm" />
      )}
      {createSessionModal && (
        <>
          <div className="fixed inset-0 z-40 backdrop-blur-sm" />
          <CreateSessionModal
            eventId={createSessionModal.eventId}
            eventName={createSessionModal.eventName}
            apiKey={apiKey || ""}
            onClose={handleCloseCreateSession}
            onSessionCreated={() => {
              handleCloseCreateSession();
              // Optionally refresh sessions for the event
            }}
          />
        </>
      )}
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between mb-8">
          <h1 className="text-4xl font-bold text-gray-900">Admin Panel</h1>
          <button
            onClick={() => {
              setApiKey(null);
              setIsAuthenticated(false);
              localStorage.removeItem("apiKey");
            }}
            className="py-2 px-4 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition-colors"
          >
            Log out
          </button>
        </div>
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-2xl font-semibold text-gray-800">All Series</h2>
          <div className="flex gap-2">
            <button
              className="px-4 py-2 bg-blue-600 text-white rounded-md font-semibold hover:bg-blue-700 transition-colors"
              onClick={() => {
                setShowNewSeriesModal(true);
                setModalError(null);
                setNewSeriesName("");
              }}
            >
              New Series
            </button>
            <button
              className="px-4 py-2 bg-green-600 text-white rounded-md font-semibold hover:bg-green-700 transition-colors"
              onClick={() => {
                setShowNewCircuitModal(true);
              }}
            >
              New Circuit
            </button>
          </div>
        </div>
        {/* Modal for new series */}
        {showNewSeriesModal && (
          <div className="fixed inset-0 z-50 flex items-center justify-center">
            <div className="bg-white rounded-lg shadow-lg p-8 w-full max-w-md relative text-black">
              <button
                className="absolute top-2 right-2 text-gray-400 hover:text-gray-600 text-2xl"
                onClick={() => setShowNewSeriesModal(false)}
                aria-label="Close"
              >
                &times;
              </button>
              <h3 className="text-xl font-bold mb-4">Create New Series</h3>
              <form onSubmit={handleCreateSeries} className="space-y-4">
                <div>
                  <label htmlFor="seriesName" className="block font-medium mb-1">Series Name</label>
                  <input
                    id="seriesName"
                    type="text"
                    value={newSeriesName}
                    onChange={e => setNewSeriesName(e.target.value)}
                    className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    required
                  />
                </div>
                <div className="flex justify-end gap-2">
                  <button
                    type="button"
                    className="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300"
                    onClick={() => setShowNewSeriesModal(false)}
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="px-4 py-2 bg-blue-600 text-white rounded-md font-semibold hover:bg-blue-700 transition-colors"
                    disabled={modalLoading}
                  >
                    {modalLoading ? <Spinner /> : "OK"}
                  </button>
                </div>
              </form>
              {modalError && (
                <div className="mt-4 text-center text-red-600 font-medium">{modalError}</div>
              )}
            </div>
          </div>
        )}
        {/* End Modal */}
        {showNewCircuitModal && (
          <CreateCircuitModal
            apiKey={apiKey || ''}
            onClose={() => setShowNewCircuitModal(false)}
            onCircuitCreated={() => {
              // Optionally refresh circuit list here if needed
              setShowNewCircuitModal(false);
            }}
          />
        )}
        {loading ? (
          <div className="flex justify-center items-center min-h-[200px]">
            <Spinner />
          </div>
        ) : error ? (
          <div className="text-red-600 text-center py-4">{error}</div>
        ) : (
          <div className="overflow-x-auto rounded-lg shadow">
            <table className="min-w-full bg-white border border-gray-200">
              <thead>
                <tr>
                  <th className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                  <th className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                  <th className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Event Count</th>
                  <th className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Years</th>
                  <th className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Events</th>
                </tr>
              </thead>
              <tbody>
                {series.map(s => [
                  <tr key={s.id} className="hover:bg-gray-50 align-top">
                    <td className="px-6 py-4 border-b text-gray-900">{s.id}</td>
                    <td className="px-6 py-4 border-b text-gray-900">{s.name}</td>
                    <td className="px-6 py-4 border-b text-gray-900">{s.eventCount}</td>
                    <td className="px-6 py-4 border-b text-gray-900">
                      <select
                        className="border rounded px-2 py-1"
                        value={selectedYear[s.id] || (s.years && s.years.length > 0 ? Math.max(...s.years) : "")}
                        onChange={e => handleYearChange(s.id, Number(e.target.value))}
                      >
                        {s.years && s.years.map(y => (
                          <option key={y} value={y}>{y}</option>
                        ))}
                      </select>
                    </td>
                    <td className="px-6 py-4 border-b text-gray-900">
                      {eventsBySeries[s.id] && eventsBySeries[s.id].length > 0 ? (
                        <span className="text-blue-600 font-medium">{eventsBySeries[s.id].length} events</span>
                      ) : (
                        <span className="text-gray-400 italic">No events</span>
                      )}
                    </td>
                  </tr>,
                  eventsBySeries[s.id] && eventsBySeries[s.id].length > 0 && (
                    <tr key={s.id + "-events"} className="">
                      <td colSpan={5} className="p-0 border-b">
                        <div className="overflow-x-auto rounded-b-lg border-t border-gray-200">
                          <table className="min-w-full bg-white text-sm border border-gray-200">
                            <thead>
                              <tr>
                                <th className="px-4 py-2 border-b text-gray-700 bg-gray-100 w-56">Event Name</th>
                                <th className="px-4 py-2 border-b text-gray-700 bg-gray-100 w-32 text-center">Start Date</th>
                                <th className="px-4 py-2 border-b text-gray-700 bg-gray-100 w-32 text-center">End Date</th>
                                <th className="px-4 py-2 border-b text-gray-700 bg-gray-100 w-40 text-center">Create Session</th>
                              </tr>
                            </thead>
                            <tbody>
                              {eventsBySeries[s.id].map(ev => (
                                <React.Fragment key={ev.eventId}>
                                  <tr key={ev.eventId}>
                                    <td className="px-4 py-2 border-b text-gray-900 w-56">{ev.name}</td>
                                    <td className="px-4 py-2 border-b text-gray-900 w-32 text-center">{ev.startDate}</td>
                                    <td className="px-4 py-2 border-b text-gray-900 w-32 text-center">{ev.endDate}</td>
                                    <td className="px-4 py-2 border-b text-center w-40">
                                      <button
                                        className="inline-flex items-center px-3 py-1 bg-blue-600 text-white text-xs font-medium rounded hover:bg-blue-700 transition-colors"
                                        onClick={() => handleOpenCreateSession(ev.eventId, ev.name)}
                                      >
                                        + Create Session
                                      </button>
                                    </td>
                                  </tr>
                                  {/* Sessions sub-table */}
                                  <tr>
                                    <td colSpan={4} className="p-0 border-b bg-gray-50">
                                      <EventSessionsTable eventId={ev.eventId} apiKey={apiKey || ""} />
                                    </td>
                                  </tr>
                                </React.Fragment>
                              ))}
                            </tbody>
                          </table>
                        </div>
                      </td>
                    </tr>
                  )
                ])}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}

function EventSessionsTable({ eventId, apiKey }: { eventId: number, apiKey: string }) {
  const [sessions, setSessions] = React.useState<import('@/lib/api').SessionDTO[] | null>(null);
  const [loading, setLoading] = React.useState(false);
  const [error, setError] = React.useState<string | null>(null);

  React.useEffect(() => {
    let mounted = true;
    setLoading(true);
    setError(null);
    setSessions(null);
    import('@/lib/api').then(api => {
      api.apiRequest(`/events/${eventId}/sessions`, {
        headers: { 'X-API-Key': apiKey },
      })
        .then((data) => {
          const sessionsData = data as import('@/lib/api').SessionsResponseDTO;
          if (mounted) setSessions(sessionsData.sessions);
        })
        .catch((err: any) => {
          if (mounted) setError(err?.message || 'Failed to fetch sessions');
        })
        .finally(() => {
          if (mounted) setLoading(false);
        });
    });
    return () => { mounted = false; };
  }, [eventId, apiKey]);

  if (loading) {
    return (
      <div className="flex items-center justify-center py-4"><Spinner /></div>
    );
  }
  if (error) {
    return (
      <div className="text-red-600 text-sm px-4 py-2">{error}</div>
    );
  }
  if (!sessions || sessions.length === 0) {
    return (
      <div className="text-gray-500 text-sm px-4 py-2">No sessions found for this event.</div>
    );
  }
  return (
    <div className="overflow-x-auto border-t border-gray-200">
      <table className="min-w-full bg-white text-xs border border-gray-200">
        <thead>
          <tr>
            <th className="px-3 py-2 border-b text-gray-700 bg-gray-100 w-48 text-center">Session Name</th>
            <th className="px-3 py-2 border-b text-gray-700 bg-gray-100 w-28 text-center">Type</th>
            <th className="px-3 py-2 border-b text-gray-700 bg-gray-100 w-44 text-center">Start Datetime</th>
            <th className="px-3 py-2 border-b text-gray-700 bg-gray-100 w-24 text-center">Duration</th>
          </tr>
        </thead>
        <tbody>
          {sessions.map(session => {
            // Format duration as '2h40m' or '6h'
            const hours = Math.floor(session.durationSeconds / 3600);
            const minutes = Math.floor((session.durationSeconds % 3600) / 60);
            let durationStr = "";
            if (hours > 0 && minutes === 0) {
              durationStr = `${hours}h`;
            } else if (hours > 0) {
              durationStr = `${hours}h${minutes}m`;
            } else {
              durationStr = `${minutes}m`;
            }
            return (
              <tr key={session.id}>
                <td className="px-3 py-2 border-b text-gray-900 w-48 text-center">{session.name}</td>
                <td className="px-3 py-2 border-b text-gray-900 w-28 text-center">{session.type}</td>
                <td className="px-3 py-2 border-b text-gray-900 w-44 text-center">{new Date(session.startDatetime).toLocaleString()}</td>
                <td className="px-3 py-2 border-b text-gray-900 w-24 text-center">{durationStr}</td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
} 