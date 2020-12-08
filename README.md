# Popular Movies

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/749c9d8432ae4d0db7c4c9586d0e51b2)](https://app.codacy.com/manual/angela-aciobanitei/andu-kotlin-movies-final?utm_source=github.com&utm_medium=referral&utm_content=angela-aciobanitei/andu-kotlin-movies-final&utm_campaign=Badge_Grade_Dashboard) [![codebeat badge](https://codebeat.co/badges/165316b7-a3ba-4cb5-9a8e-3428d2f0d1af)](https://codebeat.co/projects/github-com-angela-aciobanitei-andu-kotlin-movies-final-master)

A movies app that fetches data from the Internet using [The Movie DB API](https://www.themoviedb.org/documentation/api?language=en-US).

## Project Specs
*   Movies are displayed via a grid of their corresponding movie poster thumbnails.
*   When user taps a movie poster, the movie details are shown: title, release date, poster, vote average, and plot synopsis.
*   The movie details screen also contains a section for displaying trailer videos and user reviews.
*   The user can toggle the sort order of the movies by: most popular, highest rated, and now playing.
*   Users can mark their favorite movies and share the movie trailer’s YouTube URLs.
*   The favorite movies are stored in a local database, and can be displayed even when offline.

## Core Features
*   Using [Android Architecture Components](https://developer.android.com/topic/libraries/architecture/) to implement the [MVVM](https://medium.com/upday-devs/android-architecture-patterns-part-3-model-view-viewmodel-e7eeee76b73b) architecture pattern
*   Handling navigation with [Navigation Component](https://developer.android.com/guide/navigation) 
*   Persisting app data with [Room](https://developer.android.com/topic/libraries/architecture/room)
*   Handling network requests with [Retrofit](https://github.com/square/retrofit) and [OkHttp](https://github.com/square/okhttp)
*   Gradually loading data on demand with [Paging Library](https://developer.android.com/topic/libraries/architecture/paging/)
*   Unit testing with [Mockito](https://site.mockito.org/) and [MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver)

## Core Libraries
*   [DataBinding](https://developer.android.com/topic/libraries/data-binding/)
*   [Navigation](https://developer.android.com/guide/navigation)
*   [Room](https://developer.android.com/topic/libraries/architecture/room) for data persistence
*   [Retrofit 2](https://github.com/square/retrofit) and [OkHttp](https://github.com/square/okhttp) for networking
*   [Paging](https://developer.android.com/topic/libraries/architecture/paging/) for data loading
*   [Moshi](https://github.com/square/moshi) for parsing JSON
*   [Glide](https://github.com/bumptech/glide) for image loading
*   [Timber](https://github.com/JakeWharton/timber) for logging
*   [Dagger 2](https://github.com/google/dagger), for dependency injection  
*   [Mockito](https://site.mockito.org/), for mocking when testing
*   [MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver), for testing HTTP clients

## Installing the App

*   Clone this repository
```
git https://github.com/angela-aciobanitei/andu-kotlin-movies-final.git
```
*   Go to [The Movie Database](https://developers.themoviedb.org/3/getting-started/introduction) page and register for an API key.
*   Import the project in Android Studio and add the TMDB API Key inside the `gradle.properties` file.
```
TMDB_API_KEY="Your API Key Here"
```

## Screenshots
<img src="/screenshots/discover_top_rated2.png" width="300"/> <img src="/screenshots/search_star_wars.png" width="300"/> <img src="/screenshots/favorites.png" width="300"/> <img src="/screenshots/movie_details_hobbit.png" width="300"/> 

