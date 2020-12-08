package com.profitmobile.valiev.cinemate.data.network

/**
 * Generic класс который содержит значение с указанием статуса загрузки.
 *
 * См.: https://github.com/android/architecture-components-samples/tree/master/GithubBrowserSample
 */
data class Resource<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {
        // Создает объект ресурса со статусом SUCCESS и некоторыми данными.
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        // Создает объект ресурса со статусом ERROR и сообщением об ошибке.
        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, msg)
        }

        // Создает объект Resource со статусом LOADING для уведомления пользовательского интерфейса.
        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }
}


/**
* Статус ресурса, который предоставляется пользовательскому интерфейсу. Обычно они создаются
* классами репозитория, где они возвращают LiveData <Resource <T>> для передачи
* вернуть последние данные в пользовательский интерфейс со статусом извлечения.
*/
enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}