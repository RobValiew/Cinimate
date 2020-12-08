package com.profitmobile.valiev.cinemate.data.paging

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.profitmobile.valiev.cinemate.data.db.Movie
import com.profitmobile.valiev.cinemate.data.network.Resource
import com.profitmobile.valiev.cinemate.data.repository.MoviesRepository
import javax.inject.Inject

/**
 * paged результат возвращаемый [MoviesRepository] искомый список фильмов
 * из TMDB используя query String.
 */
data class SearchMoviesResult @Inject constructor(
    // LiveData выгружаемых списков для наблюдения за пользовательским интерфейсом.
    val pagedList: LiveData<PagedList<Movie>>,
    // Статус сетевого запроса, который нужно показать пользователю.
    val networkState: LiveData<Resource<PagedList<Movie>>>,
    // Повторяет любые неудачные запросы.
    val retry: () -> Unit
)