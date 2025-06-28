import React, { useEffect, useState } from "react";
import Spinner from "./Spinner";
import { API_BASE_URL } from "@/lib/api";
import Select from "react-select";

interface CircuitDTO {
  id: number;
  name: string;
  location: string;
}

interface CreateSessionModalProps {
  eventId: number;
  eventName: string;
  apiKey: string;
  onClose: () => void;
  onSessionCreated: () => void;
}

const SESSION_TYPES = ["Practice", "Qualifying", "Race"];

const CreateSessionModal: React.FC<CreateSessionModalProps> = ({ eventId, eventName, apiKey, onClose, onSessionCreated }) => {
  const [circuits, setCircuits] = useState<CircuitDTO[]>([]);
  const [circuitId, setCircuitId] = useState<number | null>(null);
  const [type, setType] = useState<string>(SESSION_TYPES[0]);
  const [hours, setHours] = useState<string>("");
  const [minutes, setMinutes] = useState<string>("");
  const [startDatetime, setStartDatetime] = useState<string>("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
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

  const handleCircuitChange = (option: any) => {
    setCircuitId(option ? option.value : null);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    if (!circuitId) return setError("Please select a circuit.");
    if (!type) return setError("Please select a session type.");
    if (!startDatetime) return setError("Please select a start date and time.");
    const h = parseInt(hours) || 0;
    const m = parseInt(minutes) || 0;
    if (h === 0 && m === 0) return setError("Please enter a session duration.");
    const durationSeconds = h * 3600 + m * 60;
    setLoading(true);
    try {
      const res = await fetch(`${API_BASE_URL}/sessions`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-API-Key": apiKey
        },
        body: JSON.stringify({
          eventId,
          circuitId,
          type,
          startDatetime,
          durationSeconds
        })
      });
      if (!res.ok) {
        const data = await res.json().catch(() => ({}));
        throw new Error(data.message || "Failed to create session");
      }
      setSuccess(true);
      onSessionCreated();
    } catch (err: any) {
      setError(err.message || "Failed to create session");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div className="absolute inset-0 bg-black opacity-30 z-0" onClick={onClose} />
      <div className="relative bg-white rounded-lg shadow-lg p-6 w-full max-w-md z-10">
        <h2 className="text-xl font-bold mb-4">Create New Session</h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Event select (disabled) */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Event</label>
            <select disabled className="w-full px-3 py-2 border border-gray-300 rounded bg-gray-100 text-gray-900">
              <option>{eventName}</option>
            </select>
          </div>
          {/* Circuit select (searchable) */}
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
          {/* Type select */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Type</label>
            <select
              value={type}
              onChange={e => setType(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded text-gray-900"
              required
            >
              {SESSION_TYPES.map(t => (
                <option key={t} value={t}>{t}</option>
              ))}
            </select>
          </div>
          {/* Duration inputs */}
          <div className="flex gap-2">
            <div className="flex-1">
              <label className="block text-sm font-medium text-gray-700 mb-1">Hours</label>
              <input
                type="number"
                min="0"
                value={hours}
                onChange={e => setHours(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded text-gray-900"
                placeholder="0"
                required
              />
            </div>
            <div className="flex-1">
              <label className="block text-sm font-medium text-gray-700 mb-1">Minutes</label>
              <input
                type="number"
                min="0"
                max="59"
                value={minutes}
                onChange={e => setMinutes(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded text-gray-900"
                placeholder="0"
                required
              />
            </div>
          </div>
          {/* Start date picker */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Start Date & Time</label>
            <input
              type="datetime-local"
              value={startDatetime}
              onChange={e => setStartDatetime(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded text-gray-900"
              required
            />
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
              {loading ? <Spinner /> : "Create Session"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateSessionModal; 