package com.profitmobile.valiev.cinemate.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
* таблица для movie.
*/
@Entity(tableName = "movie")
data class Movie(
    @PrimaryKey
    val id: Long,
    val title: String?,
    val overview: String?,
    @ColumnInfo(name = "release_date")
    val releaseDate: String?,
    @ColumnInfo(name = "poster_path")
    val posterPath: String?,
    @ColumnInfo(name = "backdrop_path")
    val backdropPath: String?,
    val popularity: Double?,
    @ColumnInfo(name = "vote_average")
    val voteAverage: Double?,
    @ColumnInfo(name = "vote_count")
    val voteCount: Int?,
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean
)

/**
 * таблица для genre.
 */
@Entity(tableName = "genre")
data class Genre(
    @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "movie_id")
    val movieId: Long,
    val name:String?
)

/**
 * таблица для cast.
 */
@Entity(tableName = "credits")
data class Cast(
    @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "movie_id")
    val movieId: Long,
    @ColumnInfo(name = "actor_name")
    val actorName: String?,
    @ColumnInfo(name = "profile_image_path")
    val profileImagePath: String?
)

/**
 * таблица для review.
 */
@Entity(tableName = "review")
data class Review(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "movie_id")
    val movieId: Long,
    val author: String?,
    val content: String?,
    val url: String?
)

/**
 * таблица для trailer.
 */
@Entity(tableName = "trailer")
data class Trailer(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "movie_id")
    val movieId: Long,
    val key: String?,
    val name: String?
)