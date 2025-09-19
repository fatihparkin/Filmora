# Filmora — Movie Discovery App (Jetpack Compose + TMDB)

Filmora is a modern Android app for discovering movies: browse popular titles, filter/sort, explore by genre, view rich details (trailers, cast, reviews & similar), save favorites, and post in‑app user reviews. It’s built with **Jetpack Compose**, **MVVM + Repository**, **Hilt**, **Retrofit/OkHttp**, **Room** (offline cache), and **Firebase** (Auth, Firestore, Remote Config, Analytics).

> **Package:** `com.fatihparkin.filmora`  
> **Min SDK:** 29 (Android 10) • **Target SDK:** 35  
> **Kotlin:** 1.9.x • **Compose Compiler:** 1.5.11


---

## ✨ Features

- **Home / Popular Movies**
  - Fetches from TMDB; cached to **Room** for offline access
  - **Filters** (IMDb ≥5/7/8, year buckets) and **sorting**
- **Genres**
  - Browse categories and list movies per genre
- **Movie Details**
  - Overview, rating, rich artwork (poster/backdrop)
  - **Trailers** (YouTube), **Cast**, **TMDB Reviews**
  - **Similar Movies** carousel/list
- **Favorites**
  - Per‑user favorites stored in **Firebase Firestore**
- **User Reviews (In‑App)**
  - Authenticated users can add/edit/delete reviews (Firestore)
  - Timestamps & “Edited” indicator
- **Auth**
  - **Firebase Authentication** (Email/Password)
- **Settings**
  - Light/Dark theme toggle and extensible preferences
- **Connectivity Awareness**
  - Snackbars & graceful offline fallbacks
- **Analytics**
  - **Firebase Analytics** hooks for key user flows

---

## 🏗️ Architecture

**MVVM + Repository** layered approach with DI via **Hilt**:

```
UI (Jetpack Compose screens)
  ↕ StateFlow
ViewModel (Home, Detail, Genre, Favorite, Review, Profile)
  ↕ Repository (Movie, MovieDetail, Genre, Favorite, Review, Profile)
Data sources:
  • Network: Retrofit + OkHttp -> TMDB API
  • Local: Room (FilmoraDatabase, MovieDao) for offline popular movies
  • Cloud: Firebase (Auth, Firestore, Remote Config, Analytics)
DI Modules:
  • NetworkModule, DatabaseModule, FirebaseModule
```

**State management:** Kotlin coroutines + `StateFlow` for reactive UIs.  
**Navigation:** Navigation Compose (`NavGraph`).  
**Images:** Coil Compose.  
**Error handling:** `Result`/sealed states exposed to UI.

---

## 🧰 Tech Stack

- **UI:** Jetpack Compose (Material 3)
- **DI:** Hilt (`@HiltAndroidApp`, `@Module`/`@InstallIn`)
- **Networking:** Retrofit + Gson + OkHttp Interceptor(s)
- **Persistence:** Room (entities/DAO/DB)
- **Firebase:** Auth, Firestore, Remote Config, Analytics
- **Images:** Coil
- **Kotlin:** Coroutines, Flows

---

## 📂 Project Structure (high‑level)

```
app/src/main/java/com/fatihparkin/filmora/
├─ FilmoraApplication.kt
├─ MainActivity.kt
├─ data/
│  ├─ di/ (DatabaseModule, FirebaseModule, NetworkModule)
│  ├─ local/ (entity/MovieEntity.kt, dao/MovieDao.kt, db/FilmoraDatabase.kt)
│  ├─ remote/ (ApiService.kt, interceptors/...)
│  ├─ repository/ (Movie*, Genre*, Favorite*, Profile*, Review*)
│  └─ model/ (Movie, MovieResponse, Genre, Cast, Review, Video)
├─ presentation/
│  ├─ home/ (HomeScreen, HomeViewModel, FilterOption, SortOption)
│  ├─ detail/ (MovieDetailScreen, MovieDetailViewModel)
│  ├─ genre/ (GenreScreen, GenreMoviesScreen, GenreViewModel)
│  ├─ favorite/ (FavoriteScreen, FavoriteViewModel)
│  ├─ login/ (LoginScreen, LoginViewModel)
│  ├─ register/ (RegisterScreen, RegisterViewModel)
│  ├─ profile/ (ProfileScreen, ProfileViewModel)
│  ├─ review/ (ReviewViewModel)
│  ├─ navigation/ (NavGraph, routes)
│  └─ settings/ (SettingsScreen, SettingsViewModel)
├─ ui/theme/ (Theme, Color, Type)
└─ util/ (ApiKeyProvider, RemoteConfigHelper, ConnectivityReceiver, NetworkUtils)
```

---

## 🔒 Secrets & Configuration (Design)

- **TMDB API Key via Firebase Remote Config**  
  The app **does not** hardcode the TMDB key. It is provided by Remote Config (param key: `api_key`) and read through `ApiKeyProvider` at runtime. This allows rotating the key without an app update.
- **Firestore Data Model (Collections)**  
  - `users/{uid}/favorites/{movieId}` — per‑user favorites  
  - `users/{uid}/viewed/{movieId}` — last viewed items (for profile/history)  
  - `reviews/{movieId}/items/{reviewId}` — public in‑app reviews per movie

---

## 🗃️ Offline & Caching

- **Popular movies** cached into Room (`MovieEntity`, `MovieDao`, `FilmoraDatabase`) via repository refresh.
- When offline, Home gracefully falls back to cached results; UI shows connection feedback.

---

## 🌐 Networking (TMDB API Surface)

`ApiService` (examples):
- `GET movie/popular`
- `GET genre/movie/list`
- `GET discover/movie?with_genres={id}`
- `GET movie/{movie_id}` (detail)
- `GET movie/{movie_id}/videos` (trailers)
- `GET movie/{movie_id}/credits` (cast)
- `GET movie/{movie_id}/reviews`
- `GET movie/{movie_id}/similar`

All requests inject the API key via an interceptor/provider.

---

## 🧪 Testing (targets)

- Repository unit tests (mock Retrofit/Firestore/Room)
- Compose UI tests (navigation & state rendering)
- Offline caching integration tests (Room)

---

## 🧭 Roadmap

- Paging 3 for infinite scrolling  
- Shimmer placeholders & motion/transition polish  
- Crash monitoring (Crashlytics)  
- More analytics events & funnels  
- Localization (TR/EN) and RTL support

---

## ⚖️ License — Filmora Personal & Educational License (FPEL‑1.0)

**Copyright (c) 2025 Fatih Parkın**

