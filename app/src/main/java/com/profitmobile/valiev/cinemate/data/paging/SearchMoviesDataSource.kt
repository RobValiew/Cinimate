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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.Executor
import javax.inject.Inject


/**
 *data source который использует ключи до и после, возвращаемые в запросах страницы.
 *
 * см: https://github.com/android/architecture-components-samples/tree/master/PagingWithNetworkSample
 */
class SearchMoviesDataSource @Inject constructor (
    private val apiService: TmdbApiService,
    private val retryExecutor: Executor,
    private val query: String
) : PageKeyedDataSource<Int, Movie>() {

    var retry: (() -> Any)? = null
    val networkState = MutableLiveData<Resource<PagedList<Movie>>>()
    val initialLoad = MutableLiveData<Resource<PagedList<Movie>>>()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Movie>
    ) {
        // Отправляем состояние загрузки в пользовательский интерфейс
        networkState.postValue(Resource.loading(null))
        initialLoad.postValue(Resource.loading(null))

        // Получить первую страницу данных из TMDB API.
        val request = apiService.searchMovies(query, FIRST_PAGE_KEY)

        try {
            val response = request.execute()
            if (response.isSuccessful) {
                // Get data from response.
                val data = response.body()
                val items = data?.asMovie() ?: emptyList()

                //Нет необходимости повторно загружать данные
                retry = null

               // Передать состояние загрузки в пользовательский интерфейс
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
            // Повторная загрузка данных.
            retry = { loadInitial(params, callback)  }

            // Вывод ошибки.
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
        // Игнорируется, поскольку добавляет только к начальной загрузке.
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, Movie>
    ) {
        // Отправляем состояние загрузки в пользовательский интерфейс.
        networkState.postValue(Resource.loading(null))

        // Получение следующей страницы данных из API TMDB.
        val request = apiService.searchMovies(query, params.key)
        request.enqueue(object : Callback<NetworkMoviesResponse> {
            override fun onResponse(
                call: Call<NetworkMoviesResponse>,
                response: Response<NetworkMoviesResponse>
            ) {
                if (response.isSuccessful) {
                    // Получаем данные ответа.
                    val items = response.body()?.asMovie() ?: emptyList()

                    // Нет необходимости повторять загрузку данных.
                    retry = null

                    // Передаем данные в UI.
                    callback.onResult(items, params.key + 1)

                    // Передаем состояние загрузки UI.
                    networkState.postValue(Resource.success(null))
                } else {
                    // Повторная загрузка данных.
                    retry =  { loadAfter(params, callback) }

                    // Вывод ошибки.
                    val error = Resource.error(
                        "Error code: ${response.code()}",null)
                    networkState.postValue(error)
                }
            }

            override fun onFailure(call: Call<NetworkMoviesResponse>, throwable: Throwable) {
                // Повторная загрузка данных.
                retry = { loadAfter(params, callback) }

                // Вывод failure.
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
class SearchMoviesDataSourceFactory
@Inject constructor (
    private val apiService: TmdbApiService,
    private val retryExecutor: Executor,
    private val query: String
) : DataSource.Factory<Int, Movie>() {

    val searchMoviesDataSource = MutableLiveData<SearchMoviesDataSource>()

    override fun create(): DataSource<Int, Movie> {
        val latestSource = SearchMoviesDataSource(
            apiService,
            retryExecutor,
            query
        )
        searchMoviesDataSource.postValue(latestSource)
        return latestSource
    }
}