"use client";

import { useState, useEffect } from "react";
import { useParams } from "next/navigation";
import { apiRequest, ClassesResponseDTO, SessionsResponseDTO, ResultsResponseDTO, ResultEntryDTO } from "@/lib/api";
import Spinner from "@/app/components/Spinner";
import Link from "next/link";

interface ResultEntry {
  position: number;
  carEntryId: number;
  carNumber: string;
  teamName: string;
  carModelName: string;
  classId: number;
  className: string;
  laps: number;
  totalTime: string;
  gapToFirst: string;
  gapToPrevious: string;
  bestLapNumber: number;
  bestLapTime: string;
  bestLapMph: string;
  drivers: { driverId: number; fullName: string }[];
}

export default function ResultsPage() {
  const params = useParams();
  const { eventId, seriesId, year } = params;
  const [sessions, setSessions] = useState<SessionsResponseDTO | null>(null);
  const [classes, setClasses] = useState<ClassesResponseDTO | null>(null);
  const [sessionId, setSessionId] = useState<string>("");
  const [classId, setClassId] = useState<string>("");
  const [results, setResults] = useState<(ResultEntry & { classPosition?: number })[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Fetch sessions and classes for the event
  useEffect(() => {
    async function fetchMeta() {
      try {
        setLoading(true);
        const [sessionsData, classesData] = await Promise.all([
          apiRequest<SessionsResponseDTO>(`/events/${eventId}/sessions`),
          apiRequest<ClassesResponseDTO>(`/events/${eventId}/classes`),
        ]);
        setSessions(sessionsData);
        setClasses(classesData);
        if (sessionsData.sessions.length > 0) {
          setSessionId(sessionsData.sessions[0].id.toString());
        }
        if (classesData.classes.length > 0) {
          setClassId("");
        }
      } catch (err) {
        setError(err instanceof Error ? err.message : "Failed to fetch event data");
      } finally {
        setLoading(false);
      }
    }
    fetchMeta();
  }, [eventId]);

  // Fetch results when session or class changes
  useEffect(() => {
    async function fetchResults() {
      if (!sessionId) return;
      try {
        setLoading(true);
        setError(null);
        const data: ResultsResponseDTO = await apiRequest<ResultsResponseDTO>(`/sessions/${sessionId}/results`);
        // Compute class position and filter by class if selected
        let entries = data.results.map((entry: ResultEntryDTO) => ({
          position: entry.position,
          carEntryId: entry.carEntryId,
          carNumber: entry.carEntry.number,
          teamName: entry.carEntry.teamName || "-",
          carModelName: entry.carEntry.carModel?.fullName || entry.carEntry.carModel?.name || "-",
          classId: entry.carEntry.classId,
          className: classes?.classes.find(c => c.id === entry.carEntry.classId)?.name || "-",
          laps: entry.laps,
          totalTime: entry.totalTime,
          gapToFirst: entry.gapFirst,
          gapToPrevious: entry.gapPrevious,
          bestLapNumber: entry.flLapnum,
          bestLapTime: entry.flTime,
          bestLapMph: entry.flKph ? entry.flKph.toString() : "-",
          drivers: entry.carEntry.drivers.map(d => ({ driverId: d.driverId, fullName: d.fullName })),
        }));
        if (classId) {
          entries = entries.filter((e) => e.classId.toString() === classId);
        }
        // Compute class position
        const byClass: { [key: string]: (ResultEntry & { classPosition?: number })[] } = {};
        entries.forEach((e: ResultEntry & { classPosition?: number }) => {
          if (!byClass[e.classId]) byClass[e.classId] = [];
          byClass[e.classId].push(e);
        });
        Object.values(byClass).forEach((arr) => {
          arr.sort((a, b) => a.position - b.position);
          arr.forEach((e, idx) => (e.classPosition = idx + 1));
        });
        setResults(entries);
      } catch (err) {
        setError(err instanceof Error ? err.message : "Failed to fetch results");
      } finally {
        setLoading(false);
      }
    }
    fetchResults();
  }, [sessionId, classId]);

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
        </div>
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Results</h1>
          {sessions?.eventName && (
            <p className="text-lg text-gray-600">{sessions.eventName}</p>
          )}
        </div>
        {/* Filters */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-8">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Filters</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Session</label>
              <select
                value={sessionId}
                onChange={(e) => setSessionId(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-900 bg-white"
              >
                {sessions?.sessions.map((session) => (
                  <option key={session.id} value={session.id} className="text-gray-900">
                    {session.name} ({session.type})
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Class</label>
              <select
                value={classId}
                onChange={(e) => setClassId(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-900 bg-white"
              >
                <option value="">All Classes</option>
                {classes?.classes.map((classItem) => (
                  <option key={classItem.id} value={classItem.id} className="text-gray-900">
                    {classItem.name}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>
        {/* Results Table */}
        {loading ? (
          <div className="flex items-center justify-center min-h-[120px]">
            <Spinner />
          </div>
        ) : error ? (
          <div className="text-red-600 text-center">{error}</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50 sticky top-0 z-10">
                <tr><th className="px-1 py-1 text-center text-xs font-medium text-black uppercase tracking-wider">Pos</th><th className="px-1 py-1 text-center text-xs font-medium text-black uppercase tracking-wider whitespace-nowrap">PIC</th><th className="px-1 py-1 text-center text-xs font-medium text-black uppercase tracking-wider">#</th><th className="px-1 py-1 text-center text-xs font-medium text-black uppercase tracking-wider">Class</th><th className="px-1 py-1 text-center text-xs font-medium text-black uppercase tracking-wider">Drivers</th><th className="px-1 py-1 text-center text-xs font-medium text-black uppercase tracking-wider">Team</th><th className="px-1 py-1 text-center text-xs font-medium text-black uppercase tracking-wider">Car Model</th><th className="px-1 py-1 text-center text-xs font-medium text-black uppercase tracking-wider">Laps</th><th className="px-1 py-1 text-center text-xs font-medium text-black uppercase tracking-wider">Total Time</th><th className="px-1 py-1 text-xs font-medium text-black uppercase tracking-wider text-center" colSpan={2}>Gap</th><th className="px-1 py-1 text-xs font-medium text-black uppercase tracking-wider text-center" colSpan={3}>Best Lap</th></tr>
                <tr><th className="px-1 py-1" colSpan={1}></th><th className="px-1 py-1" colSpan={1}></th><th className="px-1 py-1" colSpan={1}></th><th className="px-1 py-1" colSpan={1}></th><th className="px-1 py-1" colSpan={1}></th><th className="px-1 py-1" colSpan={1}></th><th className="px-1 py-1" colSpan={1}></th><th className="px-1 py-1" colSpan={1}></th><th className="px-1 py-1" colSpan={1}></th><th className="px-1 py-1 text-xs font-medium text-black text-center" colSpan={1}>LEAD</th><th className="px-1 py-1 text-xs font-medium text-black text-center" colSpan={1}>PREV</th><th className="px-1 py-1 text-xs font-medium text-black text-center" colSpan={1}>#</th><th className="px-1 py-1 text-xs font-medium text-black text-center" colSpan={1}>Time</th><th className="px-1 py-1 text-xs font-medium text-black text-center" colSpan={1}>MPH</th></tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {results.map((entry) => (
                  <tr key={entry.carEntryId} className="hover:bg-gray-50">
                    <td className="px-1 py-2 font-bold text-black text-center">{entry.position}</td><td className="px-1 py-2 text-black text-center">{entry.classPosition}</td><td className="px-1 py-2 text-black text-sm font-light italic text-center">{entry.carNumber}</td><td
                      className={`px-1 py-2 text-center text-sm ${
                        entry.className === 'GTP' ? 'bg-black text-white' :
                        entry.className === 'LMP2' ? 'bg-blue-600 text-white' :
                        entry.className === 'GTDPRO' ? 'bg-red-600 text-white' :
                        entry.className === 'GTD' ? 'bg-green-600 text-white' :
                        'text-black'
                      }`}
                    >
                      {entry.className}
                    </td><td className="px-1 py-2 text-black text-center">{entry.drivers.map((d, idx) => {const [first, ...lastParts] = d.fullName.split(" ");const last = lastParts.join(" ");const abbr = `${first ? first[0] + "." : ""} ${last}`.trim();return (<span key={d.driverId} className="inline-block mr-1">{abbr}{idx < entry.drivers.length - 1 ? "," : ""}</span>);})}</td><td className="px-1 py-2 text-black text-center">{entry.teamName}</td><td className="px-1 py-2 text-black text-center">{entry.carModelName}</td><td className="px-1 py-2 text-black text-center">{entry.laps}</td><td className="px-1 py-2 text-black text-center">{entry.totalTime}</td><td className="px-1 py-2 text-black text-center">{entry.gapToFirst}</td><td className="px-1 py-2 text-black text-center">{entry.gapToPrevious}</td><td className="px-1 py-2 text-black text-center">{entry.bestLapNumber || "-"}</td><td className="px-1 py-2 text-black text-center">{entry.bestLapTime || "-"}</td><td className="px-1 py-2 text-black text-center">{entry.bestLapMph || "-"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            {results.length === 0 && (
              <div className="text-center py-8 text-gray-500">No results found for this session/class.</div>
            )}
          </div>
        )}
      </div>
    </div>
  );
} 