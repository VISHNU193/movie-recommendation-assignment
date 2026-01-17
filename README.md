# Movie Recommendation App

A simple Android app that lets you browse popular movies, search for titles, and keep track of your favorites and watchlist. Built with Kotlin and Jetpack Compose.

## What it does

- Browse popular movies from TMDB
- Search for movies by title
- View movie details including rating, release date, and overview
- Add movies to your favorites or watchlist
- Infinite scroll pagination for browsing
- Play Now button with notification

## Tech Stack

- Kotlin
- Jetpack Compose with Material 3
- Retrofit + Moshi for API calls
- Room Database for local storage
- Coil for image loading
- Navigation Compose

## A note about the API key

I know that adding the API key directly in the project is insecure, but due to time constraint I wasn't able to build an API server that would handle authentication. For a production app, you'd want to proxy requests through your own backend server.

## Getting Started

### Prerequisites

- Android Studio (Arctic Fox or later recommended)
- Android SDK 24 or higher
- A TMDB account to get an API access token

### Clone the repository

```bash
git clone https://github.com/VISHNU193/movie-recommendation-assignment.git
cd movie-recommendation-assignment
```

### Get your TMDB API Access Token

1. Go to [TMDB website](https://www.themoviedb.org/) and create an account
2. Navigate to Settings > API
3. Request an API key (you'll need to fill out a form)
4. Once approved, go to the API section and copy your **API Read Access Token** (not the API Key). It starts with `eyJ...`

### Configure the API token

Open the `gradle.properties` file in the project root directory and add your token:

```properties
TMDB_API_KEY=eyJhbGciOiJIUzI1NiJ9.your_actual_token_here...
```

Make sure there are no quotes around the token value.

### Open the project in Android Studio

1. Open Android Studio
2. Select "Open" or File > Open
3. Navigate to the cloned project folder and select it
4. Wait for Gradle to sync (this might take a few minutes the first time)

### Connect your device

**For a physical device:**

1. Enable Developer Options on your phone (Settings > About Phone > tap Build Number 7 times)
2. Enable USB Debugging in Developer Options
3. Connect your phone via USB cable
4. Accept the USB debugging prompt on your phone
5. Your device should appear in the device dropdown in Android Studio

**For an emulator:**

1. In Android Studio, go to Tools > Device Manager
2. Click "Create Device"
3. Select a phone model (Pixel 6 works well)
4. Download a system image (API 33 or 34 recommended)
5. Finish the setup and start the emulator

### Run the app

1. Make sure your device or emulator is selected in the toolbar dropdown
2. Click the green Run button (or press Shift+F10)
3. Wait for the build to complete and the app to install
4. The app should launch automatically on your device

## Project Structure

```
app/src/main/java/com/example/movierecommendation/
    MainActivity.kt          - Entry point, sets up navigation
    ViewModel.kt             - Handles app state and business logic
    TmdbApi.kt              - API interface and data models
    AppDatabase.kt          - Room database setup
    MovieDao.kt             - Database queries
    MovieEntity.kt          - Database entity
    ui/
        NavGraph.kt         - Navigation routes
        SplashScreen.kt     - Splash screen
        MoviesScreen.kt     - Main movie listing
        MovieDetailScreen.kt - Movie details
        FavoritesScreen.kt  - Saved favorites
        WatchlistScreen.kt  - Watchlist
```

## Troubleshooting

**Build fails with "API key not found"**
- Make sure you added `TMDB_API_KEY` to `gradle.properties` (not `local.properties`)
- Sync Gradle after making changes

**Empty movie list or network errors**
- Check that your API token is correct and starts with `eyJ`
- Check Logcat for errors tagged with `TMDB_API`
- Make sure you have internet connection


**App crashes on startup**
- Try Clean Project (Build > Clean Project) then Rebuild
- Invalidate caches (File > Invalidate Caches and Restart)


