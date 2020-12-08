package com.profitmobile.valiev.cinemate.util

import com.profitmobile.valiev.cinemate.data.network.*

object PojosTestUtils {

    fun createGenreList(count: Int): List<NetworkGenre> {
        return (0 until count).map {
            NetworkGenre(
                id = it.toLong(),
                name = "name$it"
            )
        }
    }

    fun createCastList(count: Int): List<NetworkCast> {
        return (0 until count).map {
            NetworkCast(
                id = it.toLong(),
                actorName = "actorName$it",
                profileImagePath = "profileImagePath$it"
            )
        }
    }

    fun createReviewsList(count: Int): List<NetworkReview> {
        return (0 until count). map {
            NetworkReview(
                id = it.toString(),
                author = "author$it",
                content = "content$it",
                url = "url$it"
            )
        }
    }
    
    fun createTrailerList(count: Int): List<NetworkVideo> {
        return (0 until count).map {
            NetworkVideo(
                id = it.toString(),
                key = "key$it",
                name = "name$it"
            )
        }
    }

    fun createMovieDetails(movieId: Long): NetworkMovieDetails {
        return NetworkMovieDetails(
            id = movieId,
            title = "title",
            overview = "overview",
            releaseDate = "2020-02-25",
            posterPath = "/d4KNaTrltq6bpkFS01pYtyXa09m.jpg",
            backdropPath = "/6xKCYgH16UuwEGAyroLU6p8HLIn.jpg",
            popularity = 31.9,
            voteAverage = 8.7,
            voteCount = 11450,
            genres = createGenreList(5),
            creditsResponse = CreditsResponse(createCastList(5)),
            videosResponse = VideosResponse(createTrailerList(5)),
            reviewsResponse = ReviewsResponse(createReviewsList(5))
        )
    }
}