import React, { useEffect, useState } from "react";
import Spinner from "./Spinner";
import { API_BASE_URL } from "@/lib/api";

export interface SessionDTO {
  id: number;
  name: string;
  type: string;
  startDatetime: string;
  durationSeconds: number;
}

interface ImportSessionModalProps {
  session: SessionDTO;
  eventName: string;
  apiKey: string;
  onClose: () => void;
}

const ImportSessionModal: React.FC<ImportSessionModalProps> = ({ session, eventName, apiKey, onClose }) => {
  const [mode, setMode] = useState<'RESULTS' | 'TIMECARD' | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [url, setUrl] = useState("");
  const [importType, setImportType] = useState<'IMSA' | 'WEC'>('IMSA');
  const [submitting, setSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState<string | null>(null);

  useEffect(() => {
    setLoading(true);
    setError(null);
    fetch(`${API_BASE_URL}/sessions/${session.id}/results`, {
      headers: { 'X-API-Key': apiKey }
    })
      .then(res => res.json())
      .then(data => {
        if (data && Array.isArray(data.results) && data.results.length > 0) {
          setMode('TIMECARD');
        } else {
          setMode('RESULTS');
        }
      })
      .catch(() => setError("Failed to check session results."))
      .finally(() => setLoading(false));
  }, [session.id, apiKey]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitError(null);
    setSubmitting(true);
    try {
      const endpoint = mode === 'RESULTS' ? '/imports/process-results' : '/imports/process-timecard';
      const res = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-API-Key': apiKey
        },
        body: JSON.stringify({
          url,
          sessionId: session.id,
          importType
        })
      });
      if (!res.ok) {
        setSubmitError('something went wrong');
      } else {
        onClose();
      }
    } catch (err) {
      setSubmitError('something went wrong');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div className="absolute inset-0 bg-black opacity-30 z-0" onClick={onClose} />
      <div className="relative bg-white rounded-lg shadow-lg p-6 w-full max-w-md z-10">
        <h2 className="text-xl font-bold mb-4">Import {mode === 'RESULTS' ? 'Results' : mode === 'TIMECARD' ? 'Timecard' : ''}</h2>
        {loading ? (
          <div className="flex justify-center items-center min-h-[100px]"><Spinner /></div>
        ) : error ? (
          <div className="text-red-600 text-sm mb-4">{error}</div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Session</label>
              <select disabled className="w-full px-3 py-2 border border-gray-300 rounded bg-gray-100 text-gray-900">
                <option>{session.name} ({eventName})</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Import URL</label>
              <input
                type="text"
                value={url}
                onChange={e => setUrl(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded text-gray-900"
                placeholder="Enter import URL..."
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Import Type</label>
              <select
                value={importType}
                onChange={e => setImportType(e.target.value as 'IMSA' | 'WEC')}
                className="w-full px-3 py-2 border border-gray-300 rounded text-gray-900"
                required
              >
                <option value="IMSA">IMSA</option>
                <option value="WEC">WEC</option>
              </select>
            </div>
            {submitError && <div className="text-red-600 text-sm">{submitError}</div>}
            <div className="flex justify-end gap-2 mt-4">
              <button
                type="button"
                className="px-4 py-2 rounded bg-gray-200 text-gray-700 hover:bg-gray-300"
                onClick={onClose}
                disabled={submitting}
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-4 py-2 rounded bg-orange-600 text-white hover:bg-orange-700 disabled:opacity-50"
                disabled={submitting}
              >
                {submitting ? <Spinner /> : `Import ${mode === 'RESULTS' ? 'Results' : 'Timecard'}`}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};

export default ImportSessionModal; 