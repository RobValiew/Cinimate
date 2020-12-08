package com.profitmobile.valiev.cinemate.data.db

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movie: Movie): Long

    @Query("SELECT * FROM movie WHERE movie.id = :movieId")
    fun getMovie(movieId: Long): LiveData<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGenres(genres: List<Genre>): List<Long>

    @Query("SELECT * FROM genre WHERE genre.movie_id = :movieId")
    fun getGenres(movieId: Long): LiveData<List<Genre>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCast(cast: List<Cast>): List<Long>

    @Query("SELECT * FROM credits WHERE credits.movie_id = :movieId")
    fun getCast(movieId: Long): LiveData<List<Cast>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReviews(reviews: List<Review>): List<Long>

    @Query("SELECT * FROM review WHERE review.movie_id = :movieId")
    fun getReviews(movieId: Long): LiveData<List<Review>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrailers(trailers: List<Trailer>): List<Long>

    @Query("SELECT * FROM trailer WHERE trailer.movie_id = :movieId")
    fun getTrailers(movieId: Long): LiveData<List<Trailer>>

    // Примечание: возвращаемое значение этого метода включает POJO с @Relation.
    // аннотация @Transaction, чтобы избежать несогласованности
    // результаты между POJO и его отношениями.
    @Transaction
    @Query("SELECT * FROM movie WHERE id = :movieId")
    fun getAllMovieDetails(movieId: Long): LiveData<MovieDetails>

    @Query("SELECT * FROM movie WHERE is_favorite = 1")
    fun getAllFavoriteMovies(): LiveData<List<Movie>>

    @Query("UPDATE movie SET is_favorite = 1 WHERE id = :movieId")
    fun favorite(movieId: Long)

    @Query("UPDATE movie SET is_favorite = 0 WHERE id = :movieId")
    fun unfavorite(movieId: Long)
}