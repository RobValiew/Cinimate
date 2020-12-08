package com.profitmobile.valiev.cinemate.data.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import com.profitmobile.valiev.cinemate.data.db.Movie
import com.profitmobile.valiev.cinemate.data.db.MovieDao
import com.profitmobile.valiev.cinemate.data.db.MovieDetails
import com.profitmobile.valiev.cinemate.data.db.MoviesDatabase
import com.profitmobile.valiev.cinemate.data.network.*
import com.profitmobile.valiev.cinemate.data.paging.FilteredMoviesDataSourceFactory
import com.profitmobile.valiev.cinemate.data.paging.FilteredMoviesResult
import com.profitmobile.valiev.cinemate.data.paging.SearchMoviesDataSourceFactory
import com.profitmobile.valiev.cinemate.data.paging.SearchMoviesResult
import com.profitmobile.valiev.cinemate.ui.movielist.discover.MoviesFilter
import com.profitmobile.valiev.cinemate.utils.AppExecutors
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class MoviesRepository @Inject constructor (
    private val appExecutors: AppExecutors,
    private val database: MoviesDatabase,
    private val movieDao: MovieDao,
    private val tmdbApiService: TmdbApiService
) {
    companion object {
        private const val PAGE_SIZE = 20
    }

    @MainThread
    fun loadFilteredMovies(filter: MoviesFilter): FilteredMoviesResult {
        // Создаём TMDB data source factory
        val sourceFactory = FilteredMoviesDataSourceFactory(
            apiService = tmdbApiService,
            retryExecutor = appExecutors.networkIO(),
            filter = filter
        )
        // Получаем paged результат (мы можем использовать функцию расширения toLiveData () Kotlin).
        // Предоставляем собственный исполнитель для сетевых запросов, иначе он будет по умолчанию
        // в пул ввода-вывода компонентов архитектуры, который также используется для доступа к диску.
        val livePagedList = sourceFactory.toLiveData(
            pageSize = PAGE_SIZE,
            fetchExecutor = appExecutors.networkIO()
        )

        // Получаем состояние сети.
        val networkState = Transformations
            .switchMap(sourceFactory.pagedDataSource) {
                it.networkState
            }

        // Получить состояние обновления.
        val refreshState = Transformations
            .switchMap(sourceFactory.pagedDataSource) {
                it.initialLoad
            }

        return FilteredMoviesResult(
            pagedList = livePagedList,
            networkState = networkState,
            retry = { sourceFactory.pagedDataSource.value?.retryAllFailed() },
            refreshState = refreshState,
            refresh = {sourceFactory.pagedDataSource.value?.invalidate()}
        )
    }

    @MainThread
    fun searchMovies(query: String): SearchMoviesResult {
        // создание data source factory.
        val sourceFactory = SearchMoviesDataSourceFactory(
            apiService = tmdbApiService,
            retryExecutor = appExecutors.networkIO(),
            query = query
        )

        // LiveData of paged lists для наблюдения UI
        val livePagedList = sourceFactory.toLiveData(
            pageSize = PAGE_SIZE,
            fetchExecutor = appExecutors.networkIO()
        )

        // Получить состояние сети
        val networkState = Transformations
            .switchMap(sourceFactory.searchMoviesDataSource) {
                it.networkState
            }

        return SearchMoviesResult(
            pagedList = livePagedList,
            networkState = networkState,
            retry = { sourceFactory.searchMoviesDataSource.value?.retryAllFailed() }
        )
    }

    fun loadMovieDetails(id: Long) : LiveData<Resource<MovieDetails>> {
        return object : NetworkBoundResource<MovieDetails, NetworkMovieDetails>(appExecutors){

            override fun createCall() = tmdbApiService.getAllMovieDetails(id)

            override fun saveCallResult(item: NetworkMovieDetails) = database.runInTransaction {
                val movie = item.asMovie()
                movieDao.insertMovie(item.asMovie())
                val genres = movieDao.insertGenres(item.asGenres()).size
                val cast = movieDao.insertCast(item.asCast()).size
                val trailers = movieDao.insertTrailers(item.asVideos()).size
                val reviews = movieDao.insertReviews(item.asReviews()).size
                Timber.d("Inserted movie ${movie.title} : $genres genres, $cast cast, $trailers trailers, $reviews reviews")
            }

            override fun shouldFetch(data: MovieDetails?): Boolean = data == null

            override fun loadFromDb() = movieDao.getAllMovieDetails(id)
        }.asLiveData()
    }

    fun favorite(id: Long?) {
        appExecutors.diskIO().execute {
            id?.let { movieDao.favorite(it) }
        }
    }

    fun unfavorite(id: Long?) {
        appExecutors.diskIO().execute {
            id?.let { movieDao.unfavorite(it) }
        }
    }

    fun getAllFavoriteMovies(): LiveData<List<Movie>> {
        return movieDao.getAllFavoriteMovies()
    }
}