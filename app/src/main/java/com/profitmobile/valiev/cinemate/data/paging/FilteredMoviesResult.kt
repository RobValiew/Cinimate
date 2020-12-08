package com.profitmobile.valiev.cinemate.data.paging

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.profitmobile.valiev.cinemate.data.db.Movie
import com.profitmobile.valiev.cinemate.data.network.Resource
import com.profitmobile.valiev.cinemate.data.repository.MoviesRepository
import com.profitmobile.valiev.cinemate.ui.movielist.discover.MoviesFilter
import javax.inject.Inject

/**
 * paged результат возвращаемый [MoviesRepository] когда загружается список фильмов
 * из TMDB используя специальный [MoviesFilter].
 */
data class FilteredMoviesResult @Inject constructor(
    // LiveData страничных списков для наблюдения пользовательского интерфейса.
    // PagedList загружает фрагменты данных или страницы. Поскольку требуется больше данных,
    // он выгружается в существующий объект PagedList. Если какие-либо загруженные данные
    // изменяется, новый экземпляр PagedList передается observable
    // держатель данных из объекта на основе LiveData или RxJava2.
    val pagedList: LiveData<PagedList<Movie>>,
    // Статус сетевого запроса, отображаемый пользователю.
    val networkState: LiveData<Resource<PagedList<Movie>>>,
    //Повторяет любые неудавшиеся запросы.
    val retry: () -> Unit,
    // Статус обновления, показываемый пользователю.
    val refreshState: LiveData<Resource<PagedList<Movie>>>,
    // Обновляет данные целиком и извлекает их с нуля.
    val refresh: () -> Unit
)