package com.profitmobile.valiev.cinemate.data.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.profitmobile.valiev.cinemate.data.db.Movie
import com.profitmobile.valiev.cinemate.data.network.NetworkMoviesResponse
import com.profitmobile.valiev.cinemate.data.network.Resource
import com.profitmobile.valiev.cinemate.data.network.TmdbApiService
import com.profitmobile.valiev.cinemate.data.network.asMovie
import com.profitmobile.valiev.cinemate.ui.movielist.discover.MoviesFilter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.Executor
import javax.inject.Inject


/**
 * data source, который использует ключи до и после, возвращаемые в запросах страницы.
 *
 * См.: https://github.com/android/architecture-components-samples/tree/master/PagingWithNetworkSample
 * https://developer.android.com/topic/libraries/architecture/paging/v3-paged-data
 */
class FilteredMoviesDataSource
@Inject constructor (
    private val apiService: TmdbApiService,
    private val retryExecutor: Executor,
    private val filter: MoviesFilter
) : PageKeyedDataSource<Int, Movie>() {

    // Сохраняем ссылку на функцию для события повтора.
    var retry: (() -> Any)? = null

    // В состоянии нет синхронизации, потому что пейджинг всегда будет вызывать
    // сначала loadInitial (), затем ждем пока он вернет success
    // значение перед вызовом loadAfter ().
    val networkState = MutableLiveData<Resource<PagedList<Movie>>>()
    val initialLoad = MutableLiveData<Resource<PagedList<Movie>>>()

    // Загружаем первую страницу
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Movie>
    ) {
        // Отправляем состояние загрузки в пользовательский интерфейс.
        networkState.postValue(Resource.loading(null))
        initialLoad.postValue(Resource.loading(null))

        // Извлекаем первую страницу данных из TMDB API.
        val request = when (filter) {
            MoviesFilter.POPULAR -> apiService.getPopularMovies(FIRST_PAGE_KEY)
            MoviesFilter.TOP_RATED -> apiService.getTopRatedMovies(FIRST_PAGE_KEY)
            MoviesFilter.NOW_PLAYING ->  apiService.getNowPlayingMovies(FIRST_PAGE_KEY)
            else -> apiService.getUpcomingMovies(FIRST_PAGE_KEY)
        }

        try {
            // При обновлении лучше выполнить синхронизацию.
            // execute () немедленно вызывает запрос и блокирует
            // пока ответ не будет обработан или пока не возникнет ошибка.
            val response = request.execute()
            if (response.isSuccessful) {
                // Get data from response
                val data = response.body()
                val items = data?.asMovie() ?: emptyList()

                // Нет необходимости повторять загрузку данных.
                retry = null

                // Передаем состояние загрузки пользовательскому интерфейсу.
                networkState.postValue(Resource.success(null))
                initialLoad.postValue(Resource.success(null))

                // Передаем данные в UI.
                callback.onResult(items, FIRST_PAGE_KEY, FIRST_PAGE_KEY + 1)
            }
            else {
                // Повторная загрузка данных.
                retry = { loadInitial(params, callback) }

                // Вывод ошибки.
                val error = Resource.error(
                    "Error code: ${response.code()}", null)
                networkState.postValue(error)
                initialLoad.postValue(error)
            }

        } catch (ioe: IOException) {

            retry = { loadInitial(params, callback)  }

            val error = Resource.error(
                ioe.message ?: "Unknown error", null)
            networkState.postValue(error)
            initialLoad.postValue(error)
        }
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, Movie>
    ) {
        // Игнорируется, поскольку  добавляет только к начальной загрузке.
    }

    // Загрузить следующую страницу
    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, Movie>
    ) {
        // Передаем состояние загрузки пользовательскому интерфейсу.
        networkState.postValue(Resource.loading(null))


        val request: Call<NetworkMoviesResponse> = when (filter) {
            MoviesFilter.POPULAR -> apiService.getPopularMovies(params.key)
            MoviesFilter.TOP_RATED -> apiService.getTopRatedMovies(params.key)
            MoviesFilter.NOW_PLAYING ->  apiService.getNowPlayingMovies(params.key)
            else -> apiService.getUpcomingMovies(params.key)
        }

        request.enqueue(object : Callback<NetworkMoviesResponse> {
            override fun onResponse(
                call: Call<NetworkMoviesResponse>,
                response: Response<NetworkMoviesResponse>
            ) {
                if (response.isSuccessful) {
                    //Получить данные ответа.
                    val items = response.body()?.asMovie() ?: emptyList()


                    retry = null


                    callback.onResult(items, params.key + 1)


                    networkState.postValue(Resource.success(null))
                } else {

                    retry =  { loadAfter(params, callback) }

                    //вывод ошибки
                    val error = Resource.error(
                        "Error code: ${response.code()}",null)
                    networkState.postValue(error)
                }
            }

            override fun onFailure(call: Call<NetworkMoviesResponse>, throwable: Throwable) {
                // снова загрузка данных.
                retry = { loadAfter(params, callback) }

                // отображение Failure.
                val error = Resource.error(
                    throwable.message ?: "Unknown error", null)
                networkState.postValue(error)
            }
        })
    }

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            retryExecutor.execute {
                it.invoke()
            }
        }
    }

    companion object {
        private const val FIRST_PAGE_KEY = 1
    }
}

/**
 * data source factory который также дает возможность наблюдать за последними созданными
 * источниками данных. Это позволяет нам передавать статус сетевого запроса и т.д. Обратно в пользовательский интерфейс..
 */
class FilteredMoviesDataSourceFactory
@Inject constructor (
    private val apiService: TmdbApiService,
    private val retryExecutor: Executor,
    private val filter: MoviesFilter
) : DataSource.Factory<Int, Movie>() {

    val pagedDataSource = MutableLiveData<FilteredMoviesDataSource>()

    override fun create(): DataSource<Int, Movie> {
        val latestSource = FilteredMoviesDataSource(
            apiService,
            retryExecutor,
            filter
        )
        pagedDataSource.postValue(latestSource)
        return latestSource
    }
}