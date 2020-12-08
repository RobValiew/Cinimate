package com.profitmobile.valiev.cinemate.data.repository

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.profitmobile.valiev.cinemate.data.network.*
import com.profitmobile.valiev.cinemate.utils.AppExecutors

/**
 * Generic class который может предоставить ресурс, поддерживаемый как базой данных SQLite, так и сетью.
 * Он определяет два параметра типа, ResultType и RequestType, поскольку тип данных, возвращаемый из
 * API может не соответствовать типу данных, используемому локально.
 *
 * См. Https://developer.android.com/jetpack/docs/guide#addendum.
 * См .: https://github.com/android/android-architecture-components/tree/master/GithubBrowserSample
 *
 * @param <ResultType> Тип данных ресурса.
 * @param <RequestType> Тип ответа API.
 */
abstract class NetworkBoundResource<ResultType, RequestType>
@MainThread constructor(private val appExecutors: AppExecutors) {

    // final result LiveData.
    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        // Send loading state to the UI.
        result.value =
            Resource.loading(null)

        // Получите кешированные данные из базы данных.
        @Suppress("LeakingThis")
        val dbSource = loadFromDb()

        // прослушивание данных базы
        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { newData ->
                    setValue(Resource.success(newData))
                }
            }
        }
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        // Создаем вызов API для загрузки данных с сайта themoviedb.org.
        val apiResponse = createCall()

        // Повторно подключить базу данных в качестве нового источника, она быстро отправит ее последнее значение.
        result.addSource(dbSource) { newData ->
            setValue(Resource.loading(newData))
        }

        //  ответ API.
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            when (response) {
                // Если сетевой вызов завершается успешно, сохраняем ответ
                // в базу данных и повторно инициализируем поток.
                is ApiSuccessResponse -> {
                    appExecutors.diskIO().execute {
                        saveCallResult(processResponse(response))
                        appExecutors.mainThread().execute {
                            // специально запрашиваем новые живые данные, иначе
                            // получаем сразу последнее кешированное значение, которое может не
                            // быть в курсе последних результатов, полученных из сети
                            result.addSource(loadFromDb()) { newData ->
                                setValue(Resource.success(newData))
                            }
                        }
                    }
                }
                // Если ответ пуст, перезагружаем с диска все, что у нас было.
                is ApiEmptyResponse -> {
                    appExecutors.mainThread().execute {
                        result.addSource(loadFromDb()) { newData ->
                            setValue(Resource.success(newData))
                        }
                    }
                }
                // Если сетевой запрос не работает, отправьте ошибку напрямую.
                is ApiErrorResponse -> {
                    onFetchFailed()
                    result.addSource(dbSource) { newData ->
                        setValue(Resource.error(response.errorMessage, newData))
                    }
                }
            }
        }
    }

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<RequestType>) = response.body

    /**
    * Вызывается для создания вызова API.
    */
    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>

    /**
     * Вызывается для сохранения результата ответа API в базе данных.
     */
    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType)

    protected open fun onFetchFailed() {}

    /**
     * Вызывается с данными в базе данных, чтобы решить, следует ли получать
     * потенциально обновленные данные из сети.
     */
    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    /**
     *  Вызывается для получения кэшированных данных из базы данных.
     */
    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    /**
     * Возвращает объект LiveData, представляющий ресурс, который
     * реализовано в базовом классе
     */
    fun asLiveData() = result as LiveData<Resource<ResultType>>
}


