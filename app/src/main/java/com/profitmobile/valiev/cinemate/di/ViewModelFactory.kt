package com.profitmobile.valiev.cinemate.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * factory class для создания ViewModels. Dagger 2 позволяет использовать мульти-привязки и
 * с его помощью можно создавать «карту» объектов с определенным ключом:
 * ключ: например тип класса ViewModel - MyViewModel :: class
 * значение: экземпляр ViewModel - MyViewModel (репозиторий)
 *
 * https://github.com/android/android-architecture-components/tree/GithubBrowserSample
 * https://blog.kotlin-academy.com/understanding-dagger-2-multibindings-viewmodel-8418eb372848
 */

@Singleton
class ViewModelFactory @Inject constructor(
    // Maps Class который расширяет ViewModel как ключ, и ViewModel Provider
    // (специфичный для Dagger 2 класс, который позволяет нам предоставлять - и таким образом создавать -
    // класс, внедренный зависимостью) как значение.
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
         val creator = creators[modelClass] ?:
            creators.entries.firstOrNull {
                modelClass.isAssignableFrom(it.key)
            }?.value ?: throw IllegalArgumentException("unknown model class $modelClass")
        try {
            @Suppress("UNCHECKED_CAST")
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}