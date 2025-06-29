# Racing Stat Analysis Frontend

This is the frontend web application for Racing Stat Analysis, built with Next.js.

## Getting Started

1. Install Node.js 18+ and pnpm (or npm).
2. From the `frontend` directory, run:
   
   ```sh
   pnpm install
   pnpm dev
   ```

The app will start on http://localhost:3000.

## Features

- **Series Overview**: View all available racing series with their event counts
- **Year-based Navigation**: Browse events by year for each series
- **Event Details**: See detailed information about events including participating teams and drivers
- **Lap Time Analysis**: Comprehensive lap time analysis with filtering and pagination
- **Responsive Design**: Modern, mobile-friendly interface built with Tailwind CSS

## Pages

### 1. Landing Page (`/`)
- Displays all available racing series
- Shows years with data for each series
- Links to year-specific event pages

### 2. Year Page (`/series/[seriesId]/[year]`)
- Lists all events for a specific series and year
- Shows event details including dates and descriptions
- Displays participating teams and drivers
- Links to lap time analysis for each event

### 3. Analysis Page (`/analysis/[eventId]`)
- Comprehensive lap time analysis for a specific event
- Filterable by session ID, car ID, class ID, and percentage
- Paginated results with 50 items per page
- Overall statistics and individual driver analysis

## API Integration

The application integrates with a racing statistics API with the following endpoints:

- `GET /api/v1/series` - Get all series
- `GET /api/v1/series/{seriesId}/years` - Get years for a series
- `GET /api/v1/series/{seriesId}/{year}/events` - Get events for a series/year
- `GET /api/v1/series/events/{eventId}/teams` - Get teams for an event
- `GET /api/v1/series/events/{eventId}/laptimeanalysis` - Get lap time analysis

## Development

### Project Structure

```
src/
├── app/
│   ├── page.tsx                    # Landing page
│   ├── series/[seriesId]/[year]/
│   │   └── page.tsx               # Year page
│   ├── analysis/[eventId]/
│   │   └── page.tsx               # Analysis page
│   ├── layout.tsx                 # Root layout
│   └── globals.css                # Global styles
└── lib/
    └── api.ts                     # API utilities
```

### Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run start` - Start production server
- `npm run lint` - Run ESLint

## Technologies Used

- **Next.js 15** - React framework with App Router
- **TypeScript** - Type safety
- **Tailwind CSS** - Styling
- **React Hooks** - State management

## API Configuration

The application expects the API to be available at `/api/v1` by default. You can configure a different base URL by setting the `NEXT_PUBLIC_API_BASE_URL` environment variable.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.
