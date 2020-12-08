package com.profitmobile.valiev.cinemate.data.network

import com.profitmobile.valiev.cinemate.data.db.Movie
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Ответ от themoviedb.org при запросе списка фильмов,
 *  most popular, top rated, now playing, and upcoming.
 *
 * см: https://developers.themoviedb.org/3/movies/get-popular-movies
 * см: https://developers.themoviedb.org/3/movies/get-top-rated-movies
 * см: https://developers.themoviedb.org/3/movies/get-upcoming
 */
@JsonClass(generateAdapter = true)
data class NetworkMoviesResponse(
    @Json(name = "page")
    val currentPage: Int,
    @Json(name = "total_pages")
    val totalPages: Int,
    @Json (name = "total_results")
    val totalResults: Int,
    @Json(name = "results")
    val movies: List<NetworkMovie>
)

/**
 * Объект передачи данных, представляющий Movie result.
 * Объекты передачи данных несут ответственность за анализ ответов
 * с сервера или объекты форматирования для отправки на сервер.
 */
@JsonClass(generateAdapter = true)
data class NetworkMovie(
    val id: Long,
    val title: String?,
    val overview: String?,
    @Json(name = "release_date")
    val releaseDate: String?,
    @Json(name = "poster_path")
    val posterPath: String?,
    @Json(name = "backdrop_path")
    val backdropPath: String?,
    val popularity: Double?,
    @Json(name = "vote_average")
    val voteAverage: Double?,
    @Json(name = "vote_count")
    val voteCount: Int?
)

/**
 * Преобразует результаты сети в список объектов [Movie] базы данных.
 */
fun NetworkMoviesResponse.asMovie(): List<Movie> {
    return movies.map {
        Movie(
            id = it.id,
            title = it.title,
            overview = it.overview,
            releaseDate = it.releaseDate,
            posterPath = it.posterPath,
            backdropPath = it.backdropPath,
            popularity = it.popularity,
            voteAverage = it.voteAverage,
            voteCount = it.voteCount,
            isFavorite = false
        )
    }
}




