package com.profitmobile.valiev.cinemate.data.network

import com.profitmobile.valiev.cinemate.data.db.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
* Ответ от themoviedb.org с подробностями о фильме.
*
* Примечание. Поскольку метод "movie" поддерживает параметр запроса
* append_to_response, можно использовать его для отправки нескольких запросов в
* чтобы включить ответ титры фильмов, видео и обзоры.
*
* См .: https://developers.themoviedb.org/3/movies/get-movie-details
* См .: https://developers.themoviedb.org/3/getting-started/append-to-response
*/
@JsonClass(generateAdapter = true)
data class NetworkMovieDetails(
    @Json(name = "id")
    val id: Long,
    val title: String,
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
    val voteCount: Int?,
    @Json(name = "genres")
    val genres: List<NetworkGenre>,
    @Json(name = "credits")
    val creditsResponse: CreditsResponse,
    @Json(name = "videos")
    val videosResponse: VideosResponse,
    @Json(name = "reviews")
    val reviewsResponse: ReviewsResponse
)

/**
 *  Data Transfer Object представляющий  genre.
 */
data class NetworkGenre(
    val id: Long,
    val name: String?
)

/**
* Ответ от themoviedb.org о титрах фильмов.
* См .: https://developers.themoviedb.org/3/movies/get-movie-credits
*/
data class CreditsResponse(
    @Json(name = "cast")
    val cast: List<NetworkCast>
)

/**
 *  Data Transfer Object представляющий  cast.
 */
data class NetworkCast(
    @Json(name = "id")
    val id: Long,
    @Json(name = "name")
    val actorName: String?,
    @Json(name = "profile_path")
    val profileImagePath: String?
)

/**
* Ответ от themoviedb.org на обзоры фильмов.
* См .: https://developers.themoviedb.org/3/movies/get-movie-reviews
*/
data class ReviewsResponse(
    @Json(name = "results")
    val reviews: List<NetworkReview>
)

/**
 *  Data Transfer Object представляющий review.
 */
data class NetworkReview(
    val id: String,
    val author: String?,
    val content: String?,
    val url: String?
)

/**
* Ответ от themoviedb.org при получении видео, связанных с определенным фильмом.
* См. Https://developers.themoviedb.org/3/movies/get-movie-videos.
*/
data class VideosResponse(
    @Json(name = "results")
    val videos: List<NetworkVideo>
)

/**
 * Data Transfer Object представляющий video.
 */
data class NetworkVideo(
    val id: String,
    val key: String?,
    val name: String?
)

/**
 * Конвертирует movie details отклик сети в [Movie] database object.
 */
fun NetworkMovieDetails.asMovie(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        posterPath = posterPath,
        backdropPath = backdropPath,
        popularity = popularity,
        voteAverage = voteAverage,
        voteCount = voteCount,
        isFavorite = false
    )
}

/**
 *Конвертирует movie details отклик сети в список database objects [Genre]
 */
fun NetworkMovieDetails.asGenres(): List<Genre> {
    return genres.map {
        Genre(
            id = it.id,
            movieId = id,
            name = it.name
        )
    }.toList()
}

/**
 * Конвертирует movie details отклик сети в список database objects, [Cast]
 */
fun NetworkMovieDetails.asCast(): List<Cast> {
    return creditsResponse.cast.map {
        Cast(
            id = it.id,
            movieId = id,
            actorName = it.actorName,
            profileImagePath = it.profileImagePath
        )
    }.toList()
}

/**
 * Конвертирует movie details отклик сети в список database objects, [Trailer]
 */
fun NetworkMovieDetails.asVideos(): List<Trailer> {
    return videosResponse.videos.map {
        Trailer(
            id = it.id,
            movieId = id,
            key = it.key,
            name = it.name
        )
    }.toList()
}

/**
 * Конвертирует movie details отклик сети в список database objects, [Review]
 */
fun NetworkMovieDetails.asReviews(): List<Review> {
    return reviewsResponse.reviews.map {
        Review(
            id = it.id,
            movieId = id,
            author = it.author,
            content = it.content,
            url = it.url
        )
    }.toList()
}

/**
 * Конвертирует movie details отклик сети в  [MovieDetails]
 */
fun NetworkMovieDetails.asMovieDetails(): MovieDetails {
    return MovieDetails(
        movie = asMovie(),
        genres = asGenres(),
        cast = asCast(),
        trailers = asVideos(),
        reviews = asReviews()
    )
}





