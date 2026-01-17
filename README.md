# Movie Recommendation App

A simple Android app that lets you browse popular movies, search for titles, and keep track of your favorites and watchlist. Built with Kotlin and Jetpack Compose.

## App Demo Video

https://github.com/user-attachments/assets/274a2897-6104-4585-a875-285cf2006217

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

I know that adding the API key directly in the project is insecure, but due to time constraint I wasn't able to build an API server that would handle authentication. For a production app, you would want to proxy requests through your own backend server.

## Getting Started

### Prerequisites

- Android Studio (Arctic Fox or later recommended)
- Android SDK 24 or higher
- A TMDB account to get an API access token
- Git installed on your machine

### Step 1: Clone the repository

Open your terminal and run:

```bash
git clone https://github.com/VISHNU193/movie-recommendation-assignment.git
```

Navigate into the project folder:

```bash
cd movie-recommendation-assignment
```

### Step 2: Get your TMDB API Access Token

1. Go to https://www.themoviedb.org/ and create an account (or log in if you already have one)
2. Once logged in, click on your profile icon in the top right corner
3. Go to Settings
4. Click on "API" in the left sidebar
5. If you don't have an API key yet, click "Create" or "Request an API Key"
6. Fill out the form (for "Type of Use" you can select "Personal" or "Education")
7. Once approved, you will see two values:
   - API Key (a short alphanumeric string) - DO NOT use this one
   - API Read Access Token (a long string starting with `eyJ...`) - USE THIS ONE
8. Copy the **API Read Access Token**

Here is what the API page looks like:

![TMDB API Page](https://github.com/user-attachments/assets/aeba4d66-e2fe-4b8d-b06c-b9a848908e8d)

### Step 3: Configure the API token in the project

Open the `gradle.properties` file located in the project root directory. You can do this from the terminal:

```bash
nano gradle.properties
```

Or open it with any text editor. Add this line at the end of the file:

```properties
TMDB_API_KEY=eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI...your_full_token_here
```

Replace the value with your actual API Read Access Token. Make sure:
- There are no quotes around the token
- There are no spaces before or after the `=` sign
- The token starts with `eyJ`

Save the and close the file.

### Step 4: Open the project in Android Studio

1. Open Android Studio
2. Click on "Open" (or go to File > Open)
3. Navigate to the `movie-recommendation-assignment` folder you cloned
4. Select the folder and click "OK"
5. Android Studio will start syncing Gradle. Wait for this to complete (you can see the progress in the bottom status bar)

If Gradle sync fails, try:
```
File > Sync Project with Gradle Files
```

### Step 5: Connect your Android device

You have two options: use a physical phone or create an emulator.

**Option A: Physical Android Phone**

1. On your phone, go to Settings > About Phone
2. Tap "Build Number" 7 times to enable Developer Options
3. Go back to Settings > Developer Options (or Settings > System > Developer Options)
4. Enable "USB Debugging"
5. Connect your phone to your computer with a USB cable
6. A popup will appear on your phone asking to "Allow USB debugging" - tap "Allow"
7. In Android Studio, your device should now appear in the device dropdown in the toolbar

To verify your device is connected, run this in your terminal:

```bash
adb devices
```

You should see your device listed.

**Option B: Android Emulator**

1. In Android Studio, go to Tools > Device Manager
2. Click the "+" button or "Create Device"
3. Select a phone model (I recommend Pixel 6 or Pixel 7)
4. Click "Next"
5. Download a system image by clicking the download icon next to it (API 33 or 34 recommended)
6. Once downloaded, select it and click "Next"
7. Give your emulator a name and click "Finish"
8. Click the play button next to your new emulator to start it

### Step 6: Build and run the app

Once your device or emulator is ready:

1. Make sure your device is selected in the device dropdown in Android Studio's toolbar
2. Click the green "Run" button (triangle icon) or press `Shift + F10`
3. Wait for the build to complete
4. The app will automatically install and launch on your device

You can also build from the terminal:

```bash
./gradlew assembleDebug
```

And install the APK manually:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Project Structure

```
app/src/main/java/com/example/movierecommendation/
    MainActivity.kt          - Entry point, sets up navigation
    ViewModel.kt             - Handles app state and business logic
    TmdbApi.kt              - API interface and data models
    AppDatabase.kt          - Room database setup
    MovieDao.kt             - Database queries
    MovieEntity.kt          - Database entity
    ui/screens/
        NavGraph.kt         - Navigation routes
        SplashScreen.kt     - Splash screen
        MoviesScreen.kt     - Main movie listing
        MovieDetailScreen.kt - Movie details
        FavoritesScreen.kt  - Saved favorites
        WatchlistScreen.kt  - Watchlist
```

## Troubleshooting

**Gradle sync fails**
- Make sure you have a stable internet connection
- Try File > Invalidate Caches and Restart
- Delete the `.gradle` folder in the project root and sync again

**Build fails with "API key not found"**
- Make sure you added `TMDB_API_KEY` to `gradle.properties` (not `local.properties`)
- Check that the token has no quotes and no extra spaces
- Click File > Sync Project with Gradle Files

**Empty movie list or network errors**
- Verify your API token is correct and starts with `eyJ`
- Make sure your device has internet connection
- Check Logcat in Android Studio and filter by `TMDB_API` tag to see API errors

**App crashes on startup**
- Go to Build > Clean Project, then Build > Rebuild Project
- If that doesn't work, try File > Invalidate Caches and Restart

**Device not showing up**
- For physical devices: make sure USB Debugging is enabled and you accepted the prompt
- Try a different USB cable (some cables only charge and don't transfer data)
- Run `adb kill-server` then `adb start-server` in terminal


