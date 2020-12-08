package com.profitmobile.valiev.cinemate.ui.movielist.discover

/**
 * Используется для фильтрации фильмов, загруженных из TMDB.
 */
enum class MoviesFilter(val value: String) {
    POPULAR("popular"),
    TOP_RATED("top_rated"),
    NOW_PLAYING("now_playing"),
    UPCOMING("upcoming")
}