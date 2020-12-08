package com.profitmobile.valiev.cinemate.util

import com.profitmobile.valiev.cinemate.data.db.*


object PojosTestUtil {

    fun createGenericMovie(id: Long) = Movie(
        id = id,
        title = "title",
        overview = "overview",
        releaseDate = "2020-02-25",
        posterPath = "/d4KNaTrltq6bpkFS01pYtyXa09m.jpg",
        backdropPath = "/6xKCYgH16UuwEGAyroLU6p8HLIn.jpg",
        popularity = 31.9,
        voteAverage = 8.7,
        voteCount = 11450,
        isFavorite = false
    )
}