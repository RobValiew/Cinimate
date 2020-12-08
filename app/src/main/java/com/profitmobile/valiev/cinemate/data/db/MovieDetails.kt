package com.profitmobile.valiev.cinemate.data.db

import androidx.room.*

/**
* Простое POJO, содержащее подробную информацию о фильме, трейлеры, актерский состав и обзоры.
*
* https://developer.android.com/training/data-storage/room/relationships#one-to-many
*/

data class MovieDetails(
    @Embedded
    val movie: Movie,

    @Relation(
        parentColumn = "id",
        entityColumn = "movie_id",
        entity = Genre::class)
    val genres: List<Genre>,

    @Relation(
        parentColumn = "id",
        entityColumn = "movie_id",
        entity = Cast::class)
    val cast: List<Cast>,

    @Relation(
        parentColumn = "id",
        entityColumn = "movie_id",
        entity = Trailer::class)
    val trailers: List<Trailer>,

    @Relation(
        parentColumn = "id",
        entityColumn = "movie_id",
        entity = Review::class)
    val reviews: List<Review>
)

