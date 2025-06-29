import React, { useState, useMemo } from "react";
import Spinner from "./Spinner";
import Select from "react-select";
import countryList from "react-select-country-list";
import { API_BASE_URL } from "@/lib/api";

interface CreateCircuitModalProps {
  apiKey: string;
  onClose: () => void;
  onCircuitCreated: () => void;
}

const CreateCircuitModal: React.FC<CreateCircuitModalProps> = ({ apiKey, onClose, onCircuitCreated }) => {
  const [newCircuit, setNewCircuit] = useState({ name: '', lengthMeters: '', country: '', location: '' });
  const [circuitModalError, setCircuitModalError] = useState<string | null>(null);
  const [circuitModalLoading, setCircuitModalLoading] = useState(false);
  const countryOptions = useMemo<{ label: string; value: string }[]>(() => countryList().getData(), []);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div className="bg-white rounded-lg shadow-lg p-8 w-full max-w-md relative text-black">
        <button
          className="absolute top-2 right-2 text-gray-400 hover:text-gray-600 text-2xl"
          onClick={onClose}
          aria-label="Close"
        >
          &times;
        </button>
        <h3 className="text-xl font-bold mb-4">Create New Circuit</h3>
        <form
          onSubmit={async (e) => {
            e.preventDefault();
            setCircuitModalLoading(true);
            setCircuitModalError(null);
            try {
              const res = await fetch(`${API_BASE_URL}/circuits`, {
                method: 'POST',
                headers: {
                  'Content-Type': 'application/json',
                  'X-API-Key': apiKey || '',
                },
                body: JSON.stringify({
                  name: newCircuit.name,
                  lengthMeters: newCircuit.lengthMeters,
                  country: newCircuit.country,
                  location: newCircuit.location,
                }),
              });
              if (res.status === 409) {
                setCircuitModalError('circuit already exists');
              } else if (!res.ok) {
                setCircuitModalError('something went wrong');
              } else {
                onCircuitCreated();
                onClose();
                setNewCircuit({ name: '', lengthMeters: '', country: '', location: '' });
              }
            } catch {
              setCircuitModalError('something went wrong');
            } finally {
              setCircuitModalLoading(false);
            }
          }}
          className="space-y-4"
        >
          <div>
            <label htmlFor="circuitName" className="block font-medium mb-1">Circuit Name</label>
            <input
              id="circuitName"
              type="text"
              value={newCircuit.name}
              onChange={e => setNewCircuit({ ...newCircuit, name: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>
          <div>
            <label htmlFor="circuitLength" className="block font-medium mb-1">Length (meters)</label>
            <input
              id="circuitLength"
              type="number"
              min="0"
              step="0.01"
              value={newCircuit.lengthMeters}
              onChange={e => setNewCircuit({ ...newCircuit, lengthMeters: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>
          <div>
            <label className="block font-medium mb-1">Country</label>
            <Select
              isClearable
              isSearchable
              options={countryOptions}
              value={countryOptions.find((opt: { label: string; value: string }) => opt.label === newCircuit.country) || null}
              onChange={(option: { label: string; value: string } | null) => setNewCircuit({ ...newCircuit, country: option ? option.label : '' })}
              placeholder="Select a country..."
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
              required
            />
          </div>
          <div>
            <label htmlFor="circuitLocation" className="block font-medium mb-1">Location</label>
            <input
              id="circuitLocation"
              type="text"
              value={newCircuit.location}
              onChange={e => setNewCircuit({ ...newCircuit, location: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>
          <div className="flex justify-end gap-2">
            <button
              type="button"
              className="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300"
              onClick={onClose}
              disabled={circuitModalLoading}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 bg-green-600 text-white rounded-md font-semibold hover:bg-green-700 transition-colors"
              disabled={circuitModalLoading}
            >
              {circuitModalLoading ? <Spinner /> : 'OK'}
            </button>
          </div>
        </form>
        {circuitModalError && (
          <div className="mt-4 text-center text-red-600 font-medium">{circuitModalError}</div>
        )}
      </div>
    </div>
  );
};

export default CreateCircuitModal; 