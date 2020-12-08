package com.profitmobile.valiev.cinemate.utils

import androidx.lifecycle.Observer


/**
 * Используется как оболочка для данных, которые предоставляются через LiveData представляет собой event.
 *
 * см: https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150
 */
open class Event<out T>(private val content: T) {

    // разрешить внешнее чтение, но не запись
    var hasBeenHandled = false
        private set

    /**
     *  Возвращает содержимое и предотвращает его повторное использование.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Возвращает контент, даже если он уже был обработан.
     */
    fun peekContent(): T = content
}


/**
 * [Observer] для [Event] упрощение схемы проверки наличия [Event]
 * контент уже обработан. [onEventUnhandledContent] вызывается * только *, если
 *  [Event] содержимое не было обработано.
 */
class EventObserver<T>(
    private val onEventUnhandledContent: (T) -> Unit
) : Observer<Event<T>> {

    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}