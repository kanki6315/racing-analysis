'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { apiRequest } from '@/lib/api';

interface Series {
  id: number;
  name: string;
  eventCount: number;
  years: number[];
}

export default function Home() {
  const [series, setSeries] = useState<Series[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Fetch all series (now includes years)
        const seriesData: Series[] = await apiRequest<Series[]>('/series');
        setSeries(seriesData);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'An error occurred');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-xl">Loading racing series...</div>
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

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-12">
          <h1 className="text-4xl font-bold text-gray-900 mb-4">
            Performance Analysis
          </h1>
          <p className="text-xl text-gray-600">
            Explore racing series data and lap time analysis
          </p>
        </div>

        <div className="grid gap-8">
          {series.map((seriesItem) => (
            <div
              key={seriesItem.id}
              className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow"
            >
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-2xl font-semibold text-gray-900">
                  {seriesItem.name}
                </h2>
                <span className="bg-blue-100 text-blue-800 text-sm font-medium px-3 py-1 rounded-full">
                  {seriesItem.eventCount} events
                </span>
              </div>

              {seriesItem.years && seriesItem.years.length > 0 ? (
                <div>
                  <h3 className="text-lg font-medium text-gray-700 mb-3">Available Years:</h3>
                  <div className="flex flex-wrap gap-3">
                    {seriesItem.years.map((year) => (
                      <Link
                        key={year}
                        href={`/series/${seriesItem.id}/${year}`}
                        className="inline-flex items-center px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-md hover:bg-blue-700 transition-colors"
                      >
                        {year}
                      </Link>
                    ))}
                  </div>
                </div>
              ) : (
                <p className="text-gray-500 italic">No data available for this series</p>
              )}
            </div>
          ))}
        </div>

        {series.length === 0 && (
          <div className="text-center py-12">
            <p className="text-gray-500 text-lg">No racing series found</p>
          </div>
        )}
      </div>
    </div>
  );
}
