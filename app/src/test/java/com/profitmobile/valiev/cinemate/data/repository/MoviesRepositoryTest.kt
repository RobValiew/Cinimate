package com.profitmobile.valiev.cinemate.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.profitmobile.valiev.cinemate.data.db.MovieDao
import com.profitmobile.valiev.cinemate.data.db.MovieDetails
import com.profitmobile.valiev.cinemate.data.db.MoviesDatabase
import com.profitmobile.valiev.cinemate.data.db.Review
import com.profitmobile.valiev.cinemate.data.network.Resource
import com.profitmobile.valiev.cinemate.data.network.TmdbApiService
import com.profitmobile.valiev.cinemate.data.network.asMovieDetails
import com.profitmobile.valiev.cinemate.util.*
import org.junit.Test

import org.junit.Before
import org.junit.Rule
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

/**
 * Test [MoviesRepository].
 */
class MoviesRepositoryTest {

    private lateinit var repository: MoviesRepository

    private val dao = Mockito.mock(MovieDao::class.java)
    private val service = Mockito.mock(TmdbApiService::class.java)

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        val db = Mockito.mock(MoviesDatabase::class.java)
        Mockito.`when`(db.movieDao).thenReturn(dao)
        Mockito.`when`(db.runInTransaction(ArgumentMatchers.any())).thenCallRealMethod()
        repository = MoviesRepository(
            appExecutors = InstantAppExecutors(),
            database = db,
            movieDao = dao,
            tmdbApiService = service)
    }


    @Test
    fun loadMovieDetailsFromNetwork() {
        val movieId = 1L
        val dbData = MutableLiveData<MovieDetails>()
        Mockito.`when`(dao.getAllMovieDetails(movieId)).thenReturn(dbData)

        val networkMovieDetails = PojosTestUtils.createMovieDetails(movieId)
        val callLiveData = ApiUtil.successCall(networkMovieDetails)
        Mockito.`when`(service.getAllMovieDetails(movieId)).thenReturn(callLiveData)

        val liveMovieDetails = repository.loadMovieDetails(movieId)
        Mockito.verify(dao).getAllMovieDetails(movieId)
        Mockito.verifyNoMoreInteractions(service)

        val observer = mock<Observer<Resource<MovieDetails>>>()
        liveMovieDetails.observeForever(observer)
        Mockito.verifyNoMoreInteractions(service)
        Mockito.verify(observer).onChanged(Resource.loading(null))

        val updatedDbData = MutableLiveData<MovieDetails>()
        Mockito.`when`(dao.getAllMovieDetails(movieId)).thenReturn(updatedDbData)

        dbData.postValue(null)
        Mockito.verify(service).getAllMovieDetails(movieId)
        val insertedReviews = argumentCaptor<List<Review>>()

        Mockito.verify(dao).insertReviews(insertedReviews.capture() ?: emptyList())

        val movieDetails = networkMovieDetails.asMovieDetails()
        updatedDbData.postValue(movieDetails)
        Mockito.verify(observer).onChanged(Resource.success(movieDetails))
    }
}