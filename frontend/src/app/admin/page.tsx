"use client";

import { useEffect, useState, useMemo, useCallback } from "react";
import { apiRequest, API_BASE_URL, fetchEventsForSeriesYear, EventDTO } from "../../lib/api";
import Spinner from "../components/Spinner";
import React from "react";
import CreateSessionModal from "../components/CreateSessionModal";
import CreateCircuitModal from '../components/CreateCircuitModal';
import ImportSessionModal, { SessionDTO } from '../components/ImportSessionModal';
import Select from "react-select";

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
  const [modalLoading, setModalLoading] = useState(false);
  const [createSessionModal, setCreateSessionModal] = React.useState<{eventId: number, eventName: string} | null>(null);
  const [showNewCircuitModal, setShowNewCircuitModal] = useState(false);
  const [showNewEventModal, setShowNewEventModal] = useState<{seriesId: number, year: number} | null>(null);
  const [activeTab, setActiveTab] = useState<'series' | 'circuits' | 'imports'>('series');
  const [search, setSearch] = useState('');

  // Circuits tab state
  const [circuits, setCircuits] = useState<unknown[]>([]);
  const [circuitsLoading, setCircuitsLoading] = useState(false);
  const [circuitsError, setCircuitsError] = useState<string | null>(null);

  // Import Jobs tab state
  const [importJobs, setImportJobs] = useState<unknown[]>([]);
  const [importJobsLoading, setImportJobsLoading] = useState(false);
  const [importJobsError, setImportJobsError] = useState<string | null>(null);

  // Validate the API key
  const validateKey = useCallback(async (key: string) => {
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
    } catch {
      setError("Failed to validate API key.");
      setIsAuthenticated(false);
      setApiKey(null);
    } finally {
      setLoading(false);
    }
  }, []);

  // Check for apiKey in localStorage and validate
  useEffect(() => {
    const storedKey = localStorage.getItem("apiKey");
    if (storedKey) {
      validateKey(storedKey);
    }
  }, [validateKey]);

  // Fetch all series
  const fetchSeries = async (key: string) => {
    setLoading(true);
    setError(null);
    try {
      const data = await apiRequest<Series[]>("/series", {
        headers: { "X-API-Key": key },
      });
      setSeries(data);
    } catch {
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
    } catch {
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
     
  }, [series, apiKey]);

  // Handle year change for a series
  const handleYearChange = (seriesId: number, year: number) => {
    setSelectedYear(prev => ({ ...prev, [seriesId]: year }));
    if (apiKey) loadEvents(seriesId, year, apiKey);
  };

  // Handle API key form submit
  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    validateKey(inputKey);
  };

  // Create new series
  const handleCreateSeries = async (event: React.FormEvent) => {
    event.preventDefault();
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
        setModalLoading(false);
        setShowNewSeriesModal(false);
        setNewSeriesName("");
        // Refresh series list
        fetchSeries(apiKey!);
      }
    } catch {
      setModalError("Something went wrong");
    } finally {
      setModalLoading(false);
    }
  };

  const handleOpenCreateSession = (eventId: number, eventName: string) => setCreateSessionModal({ eventId, eventName });
  const handleCloseCreateSession = () => setCreateSessionModal(null);

  useEffect(() => {
    if (activeTab === 'circuits' && apiKey) {
      setCircuitsLoading(true);
      setCircuitsError(null);
      fetch(`${API_BASE_URL}/circuits`, {
        headers: { 'X-API-Key': apiKey }
      })
        .then(res => {
          if (!res.ok) throw new Error('Failed to fetch circuits');
          return res.json();
        })
        .then(data => {
          setCircuits(Array.isArray(data) ? data : []);
        })
        .catch(() => setCircuitsError('Failed to fetch circuits'))
        .finally(() => setCircuitsLoading(false));
    }
  }, [activeTab, apiKey, showNewCircuitModal]);

  // Filter circuits by search
  const filteredCircuits = useMemo(() => {
    if (!search) return circuits;
    const s = search.toLowerCase();
    return circuits.filter((c) => {
      const circuit = c as Record<string, unknown>;
      return (
        typeof circuit.name === 'string' && circuit.name.toLowerCase().includes(s)
        || typeof circuit.location === 'string' && circuit.location.toLowerCase().includes(s)
        || typeof circuit.country === 'string' && circuit.country.toLowerCase().includes(s)
      );
    });
  }, [circuits, search]);

  useEffect(() => {
    if (activeTab === 'imports' && apiKey) {
      setImportJobsLoading(true);
      setImportJobsError(null);
      fetch(`${API_BASE_URL}/imports`, {
        headers: { 'X-API-Key': apiKey }
      })
        .then(res => {
          if (!res.ok) throw new Error('Failed to fetch import jobs');
          return res.json();
        })
        .then(data => {
          setImportJobs(Array.isArray(data) ? data : []);
        })
        .catch(() => setImportJobsError('Failed to fetch import jobs'))
        .finally(() => setImportJobsLoading(false));
    }
  }, [activeTab, apiKey]);

  // Filter import jobs by search
  const filteredImportJobs = useMemo(() => {
    if (!search) return importJobs;
    const s = search.toLowerCase();
    return importJobs.filter((job) => {
      const j = job as Record<string, unknown>;
      return (
        typeof j.url === 'string' && j.url.toLowerCase().includes(s)
        || typeof j.status === 'string' && j.status.toLowerCase().includes(s)
      );
    });
  }, [importJobs, search]);

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
        {/* Tabs and search bar */}
        <div className="flex items-center justify-between mb-6">
          <div className="flex gap-2 items-center">
            <button
              className={`px-4 py-2 rounded-t-md font-semibold border-b-2 transition-colors ${activeTab === 'series' ? 'border-blue-600 text-blue-700 bg-white' : 'border-transparent text-gray-600 bg-gray-100 hover:bg-gray-200'}`}
              onClick={() => setActiveTab('series')}
            >
              Series
            </button>
            <button
              className={`px-4 py-2 rounded-t-md font-semibold border-b-2 transition-colors ${activeTab === 'circuits' ? 'border-blue-600 text-blue-700 bg-white' : 'border-transparent text-gray-600 bg-gray-100 hover:bg-gray-200'}`}
              onClick={() => setActiveTab('circuits')}
            >
              Circuits
            </button>
            <button
              className={`px-4 py-2 rounded-t-md font-semibold border-b-2 transition-colors ${activeTab === 'imports' ? 'border-blue-600 text-blue-700 bg-white' : 'border-transparent text-gray-600 bg-gray-100 hover:bg-gray-200'}`}
              onClick={() => setActiveTab('imports')}
            >
              Import Jobs
            </button>
          </div>
          <input
            type="text"
            className="border px-3 py-2 rounded-md w-64 focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder={activeTab === 'series' ? 'Search series...' : activeTab === 'circuits' ? 'Search circuits...' : 'Search import jobs...'}
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
          {/* Only show New Series or New Circuit button depending on tab */}
          <div className="flex gap-2">
            {activeTab === 'series' && (
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
            )}
            {activeTab === 'circuits' && (
              <button
                className="px-4 py-2 bg-green-600 text-white rounded-md font-semibold hover:bg-green-700 transition-colors"
                onClick={() => {
                  setShowNewCircuitModal(true);
                }}
              >
                New Circuit
              </button>
            )}
          </div>
        </div>
        {/* Tab panels */}
        <div>
          {activeTab === 'series' && (
            <div className="overflow-x-auto rounded-lg shadow">
              <table className="min-w-full bg-white border border-gray-200">
                <thead>
                  <tr>
                    <th className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                    <th className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                    <th className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Event Count</th>
                    <th className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Years</th>
                    <th className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Events</th>
                    <th className="px-6 py-3 border-b text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Add</th>
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
                      <td className="px-6 py-4 border-b text-center">
                        <button
                          className="inline-flex items-center justify-center w-6 h-6 bg-green-500 text-white rounded-full text-base font-bold hover:bg-green-600 focus:outline-none focus:ring-2 focus:ring-green-400"
                          title="Add Event"
                          onClick={() => setShowNewEventModal({ seriesId: s.id, year: selectedYear[s.id] })}
                          style={{ lineHeight: 1 }}
                        >
                          +
                        </button>
                      </td>
                    </tr>,
                    eventsBySeries[s.id] && eventsBySeries[s.id].length > 0 && (
                      <tr key={s.id + "-events"} className="">
                        <td colSpan={6} className="p-0 border-b">
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
                                        <EventSessionsTable eventId={ev.eventId} eventName={ev.name} apiKey={apiKey || ""} />
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
          {activeTab === 'circuits' && (
            <div className="overflow-x-auto rounded-lg shadow bg-white">
              {circuitsLoading ? (
                <div className="flex justify-center items-center min-h-[200px]"><Spinner /></div>
              ) : circuitsError ? (
                <div className="text-red-600 text-center py-4">{circuitsError}</div>
              ) : (
                <>
                  <table className="min-w-full bg-white border border-gray-200">
                    <thead>
                      <tr>
                        <th className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                        <th className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                        <th className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Location</th>
                        <th className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Country</th>
                        <th className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Length (m)</th>
                      </tr>
                    </thead>
                    <tbody>
                      {filteredCircuits.length === 0 ? (
                        <tr>
                          <td colSpan={5} className="text-center text-gray-500 py-8">No circuits found.</td>
                        </tr>
                      ) : (
                        filteredCircuits.map((c) => {
                          const circuit = c as Record<string, unknown>;
                          return (
                            <tr key={String(circuit.id)} className="hover:bg-gray-50">
                              <td className="px-6 py-4 border-b text-gray-900">{String(circuit.id ?? '')}</td>
                              <td className="px-6 py-4 border-b text-gray-900">{String(circuit.name ?? '')}</td>
                              <td className="px-6 py-4 border-b text-gray-900">{String(circuit.location ?? '')}</td>
                              <td className="px-6 py-4 border-b text-gray-900">{String(circuit.country ?? '')}</td>
                              <td className="px-6 py-4 border-b text-gray-900">{String(circuit.lengthMeters ?? '')}</td>
                            </tr>
                          );
                        })
                      )}
                    </tbody>
                  </table>
                </>
              )}
            </div>
          )}
          {activeTab === 'imports' && (
            <div className="overflow-x-auto rounded-lg shadow bg-white">
              {importJobsLoading ? (
                <div className="flex justify-center items-center min-h-[200px]"><Spinner /></div>
              ) : importJobsError ? (
                <div className="text-red-600 text-center py-4">{importJobsError}</div>
              ) : (
                <>
                  <table className="min-w-full bg-white border border-gray-200">
                    <thead>
                      <tr>
                        <th className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                        <th className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                        <th className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">URL</th>
                        <th className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
                        <th className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Process</th>
                        <th className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Session ID</th>
                        <th className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Completed At</th>
                        <th className="px-6 py-3 border-b text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Error</th>
                      </tr>
                    </thead>
                    <tbody>
                      {filteredImportJobs.length === 0 ? (
                        <tr>
                          <td colSpan={8} className="text-center text-gray-500 py-8">No import jobs found.</td>
                        </tr>
                      ) : (
                        filteredImportJobs.map((job) => {
                          const j = job as Record<string, unknown>;
                          return (
                            <tr key={String(j.id)} className="hover:bg-gray-50">
                              <td className="px-6 py-4 border-b text-gray-900">{String(j.id ?? '')}</td>
                              <td className="px-6 py-4 border-b text-gray-900">{String(j.status ?? '')}</td>
                              <td className="px-6 py-4 border-b text-gray-900 text-xs break-all max-w-xs whitespace-pre-line">{String(j.url ?? '')}</td>
                              <td className="px-6 py-4 border-b text-gray-900">{String(j.importType ?? '')}</td>
                              <td className="px-6 py-4 border-b text-gray-900">{String(j.processType ?? '')}</td>
                              <td className="px-6 py-4 border-b text-gray-900">{String(j.sessionId ?? '')}</td>
                              <td className="px-6 py-4 border-b text-gray-900">{j.completionTime ? new Date(String(j.completionTime)).toLocaleString() : '-'}</td>
                              <td className="px-6 py-4 border-b text-gray-900 text-xs break-all max-w-xs whitespace-pre-line">{String(j.error ?? '')}</td>
                            </tr>
                          );
                        })
                      )}
                    </tbody>
                  </table>
                </>
              )}
            </div>
          )}
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
        {showNewEventModal && (
          <CreateEventModal
            seriesId={showNewEventModal.seriesId}
            year={showNewEventModal.year}
            apiKey={apiKey || ""}
            onClose={() => setShowNewEventModal(null)}
            onEventCreated={() => {
              loadEvents(showNewEventModal.seriesId, showNewEventModal.year, apiKey!);
            }}
          />
        )}
      </div>
    </div>
  );
}

function EventSessionsTable({ eventId, eventName, apiKey }: { eventId: number, eventName: string, apiKey: string }) {
  const [sessions, setSessions] = React.useState<SessionDTO[] | null>(null);
  const [loading, setLoading] = React.useState(false);
  const [error, setError] = React.useState<string | null>(null);
  const [importModal, setImportModal] = React.useState<{ session: SessionDTO, eventName: string } | null>(null);

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
          const sessionsData = data as { sessions: SessionDTO[] };
          if (mounted) setSessions(sessionsData.sessions);
        })
        .catch((err: unknown) => {
          if (mounted) setError(err instanceof Error ? err.message : 'Failed to fetch sessions');
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
    <div className="overflow-x-auto border-t border-gray-200 relative">
      {importModal && (
        <ImportSessionModal
          session={importModal.session}
          eventName={importModal.eventName}
          apiKey={apiKey}
          onClose={() => setImportModal(null)}
        />
      )}
      {importModal && (
        <div className="fixed inset-0 z-40 backdrop-blur-sm" />
      )}
      <table className="min-w-full bg-white text-xs border border-gray-200">
        <thead>
          <tr>
            <th className="px-3 py-2 border-b text-gray-700 bg-gray-100 w-48 text-center">Session Name</th>
            <th className="px-3 py-2 border-b text-gray-700 bg-gray-100 w-28 text-center">Type</th>
            <th className="px-3 py-2 border-b text-gray-700 bg-gray-100 w-44 text-center">Start Datetime</th>
            <th className="px-3 py-2 border-b text-gray-700 bg-gray-100 w-24 text-center">Duration</th>
            <th className="px-3 py-2 border-b text-gray-700 bg-gray-100 w-24 text-center">Import</th>
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
                <td className="px-3 py-2 border-b text-center w-24">
                  <button
                    className="inline-flex items-center justify-center w-8 h-8 bg-orange-500 text-white rounded-full text-base font-bold hover:bg-orange-600 focus:outline-none focus:ring-2 focus:ring-orange-400"
                    title="Import Results/Timecard"
                    onClick={() => setImportModal({ session, eventName })}
                    style={{ lineHeight: 1 }}
                  >
                    &#8681;
                  </button>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}

function CreateEventModal({ seriesId, year, apiKey, onClose, onEventCreated }: { seriesId: number, year: number, apiKey: string, onClose: () => void, onEventCreated: () => void }) {
  const [name, setName] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [circuits, setCircuits] = useState<{ id: number; name: string; location: string }[]>([]);
  const [circuitId, setCircuitId] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [circuitsLoading, setCircuitsLoading] = useState(false);

  useEffect(() => {
    setCircuitsLoading(true);
    fetch(`${API_BASE_URL}/circuits`, {
      headers: { "X-API-Key": apiKey }
    })
      .then(res => res.json())
      .then(data => {
        setCircuits(Array.isArray(data) ? data : []);
        setCircuitsLoading(false);
      })
      .catch(() => setCircuitsLoading(false));
  }, [apiKey]);

  const circuitOptions = circuits.map(c => ({
    value: c.id,
    label: c.location && c.location.trim() ? `${c.name} (${c.location})` : c.name
  }));
  const selectedCircuit = circuitOptions.find(opt => opt.value === circuitId) || null;

  const handleCircuitChange = (option: unknown) => {
    setCircuitId(option ? (option as { value: number }).value : null);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    if (!name) return setError("Please enter an event name.");
    if (!startDate) return setError("Please select a start date.");
    if (!endDate) return setError("Please select an end date.");
    if (!circuitId) return setError("Please select a circuit.");
    setLoading(true);
    try {
      const res = await fetch(`${API_BASE_URL}/events`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-API-Key": apiKey
        },
        body: JSON.stringify({
          name,
          startDate,
          endDate,
          seriesId,
          year,
          circuitId
        })
      });
      if (!res.ok) {
        const data = await res.json().catch(() => ({}));
        throw new Error(data.message || "Failed to create event");
      }
      onEventCreated();
      onClose();
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Failed to create event");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div className="absolute inset-0 bg-black opacity-30 z-0" onClick={onClose} />
      <div className="relative bg-white rounded-lg shadow-lg p-6 w-full max-w-md z-10">
        <h2 className="text-xl font-bold mb-4">Create New Event</h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Event Name</label>
            <input
              type="text"
              value={name}
              onChange={e => setName(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded text-gray-900"
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Start Date</label>
            <input
              type="date"
              value={startDate}
              onChange={e => setStartDate(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded text-gray-900"
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">End Date</label>
            <input
              type="date"
              value={endDate}
              onChange={e => setEndDate(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded text-gray-900"
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Circuit</label>
            <Select
              isClearable
              isSearchable
              options={circuitOptions}
              value={selectedCircuit}
              onChange={handleCircuitChange}
              isLoading={circuitsLoading}
              placeholder="Select a circuit..."
              classNamePrefix="react-select"
              className="react-select-container"
              styles={{
                control: (base) => ({ ...base, minHeight: '38px', borderColor: '#d1d5db', color: '#000' }),
                menu: (base) => ({ ...base, zIndex: 9999 }),
                singleValue: (base) => ({ ...base, color: '#000' }),
                input: (base) => ({ ...base, color: '#000' }),
                option: (base, state) => ({ ...base, color: '#000', backgroundColor: state.isSelected ? '#e5e7eb' : state.isFocused ? '#f3f4f6' : '#fff' }),
                placeholder: (base) => ({ ...base, color: '#000' }),
              }}
            />
            {circuitsLoading && <div className="mt-2"><Spinner /></div>}
          </div>
          {error && <div className="text-red-600 text-sm">{error}</div>}
          <div className="flex justify-end gap-2 mt-4">
            <button
              type="button"
              className="px-4 py-2 rounded bg-gray-200 text-gray-700 hover:bg-gray-300"
              onClick={onClose}
              disabled={loading}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 rounded bg-blue-600 text-white hover:bg-blue-700 disabled:opacity-50"
              disabled={loading}
            >
              {loading ? <Spinner /> : "Create Event"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
} 