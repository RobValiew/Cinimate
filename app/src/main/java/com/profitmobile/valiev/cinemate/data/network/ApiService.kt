package com.profitmobile.valiev.cinemate.data.network

import androidx.lifecycle.LiveData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


/**
 * Определяет точки доступа REST API для Retrofit.
 */
interface TmdbApiService {

    /**
     * полоучет список текущих popular movies от TMDb.
     */
    @GET("movie/popular")
    fun getPopularMovies(@Query("page") page: Int): Call<NetworkMoviesResponse>

    /**
     * получает top rated от TMDb.
     */
    @GET("movie/top_rated")
    fun getTopRatedMovies(@Query("page") page: Int): Call<NetworkMoviesResponse>

    /**
     * получает список фильмов, идущих в кинотеатрах
     */
    @GET("movie/now_playing")
    fun getNowPlayingMovies(@Query("page") page: Int): Call<NetworkMoviesResponse>

    /**
     * получает список ожидаемых фильмов в кино.
     */
    @GET("movie/upcoming")
    fun getUpcomingMovies(@Query("page") page: Int): Call<NetworkMoviesResponse>

    /**
     * Search for movies: https://developers.themoviedb.org/3/search/search-movies
     */
    @GET("search/movie")
    fun searchMovies(@Query("query") query: String,
                     @Query("page") page: Int): Call<NetworkMoviesResponse>

    /**
    * Получить подробную информацию о фильме.
    * Используйте параметр запроса «append_to_response» для отправки нескольких запросов.
    * https://developers.themoviedb.org/3/getting-started/append-to-response
    */
    @GET("movie/{id}?append_to_response=videos,credits,reviews")
    fun getAllMovieDetails(@Path("id") id: Long): LiveData<ApiResponse<NetworkMovieDetails>>
}


