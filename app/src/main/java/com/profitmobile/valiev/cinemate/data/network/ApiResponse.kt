package com.profitmobile.valiev.cinemate.data.network

import retrofit2.Response

/**
 * Общий класс, используемый отклик API.
 * @param <T> тип response object
 *
 *
 */
sealed class ApiResponse<T> {

    companion object {

        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            return ApiErrorResponse(error.message ?: "Unknown error")
        }

        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                if (body == null || response.code() == 204) {
                    ApiEmptyResponse()
                } else {
                    ApiSuccessResponse(body = body)
                }
            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if (msg.isNullOrEmpty()) response.message() else msg

                ApiErrorResponse(errorMsg ?: "Unknown error")
            }
        }
    }
}

/**
 * Ответ API при коде состояния HTTP  204 (без содержимого)
 * и тело ответа пустое.
 */
class ApiEmptyResponse<T> : ApiResponse<T>()

/**
 * Ответ API, когда код статуса HTTP находится в диапазоне [200..300)
 * и тело ответа не равно нулю.
 */
data class ApiSuccessResponse<T>(val body: T) : ApiResponse<T>()

/**
 * Ответ API, когда код статуса HTTP больше 400
 * (например, 4xx для ошибки клиента или 5xx для ошибки сервера).
 */
data class ApiErrorResponse<T>(val errorMessage: String) : ApiResponse<T>()
