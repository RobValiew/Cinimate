package com.profitmobile.valiev.cinemate.ui.movielist.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.profitmobile.valiev.cinemate.R
import com.profitmobile.valiev.cinemate.data.repository.MoviesRepository
import com.profitmobile.valiev.cinemate.utils.Event
import javax.inject.Inject


/**
 *  [ViewModel] для [FilteredMoviesFragment].
 */
class FilteredMoviesViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository
): ViewModel() {

    //Сохраняет текущий фильтр фильма (popular, top rated, now playing, upcoming).
    private val _filter = MutableLiveData<MoviesFilter>()

    //Сохраняет текущий app bar title (Popular, Top Rated, Now Playing, Upcoming).
    private val _title = MutableLiveData<Int>()
    val title: LiveData<Int> = _title

    private val _pagedResult = Transformations
        .map(_filter) { moviesRepository.loadFilteredMovies(it) }

    val pagedData = Transformations
        .switchMap(_pagedResult) { it.pagedList }

    val networkState = Transformations
        .switchMap(_pagedResult) { it.networkState }

    val refreshState = Transformations
        .switchMap(_pagedResult) { it.refreshState }

    init {
        // Показывать популярные фильмы по умолчанию.
        _filter.value = MoviesFilter.POPULAR
        _title.value = R.string.category_popular
    }

    fun updateFilter(filter: MoviesFilter) {
        _filter.value = filter
        _title.value = when (filter) {
            MoviesFilter.TOP_RATED -> R.string.category_top_rated
            MoviesFilter.NOW_PLAYING -> R.string.category_now_playing
            MoviesFilter.UPCOMING -> R.string.category_upcoming
            else -> R.string.category_popular
        }
    }

    fun retry() {
        _pagedResult.value?.retry?.invoke()
    }

    fun refresh() {
        _pagedResult.value?.refresh?.invoke()
    }

    // Обрабатывает переход к деталям выбранного фильма.
    private val _navigateToMovieDetails = MutableLiveData<Event<Long?>>()
    val navigateToMovieDetails: LiveData<Event<Long?>>
        get() = _navigateToMovieDetails

    fun navigateToDetailsEvent(id: Long?) {
        // Запустить Event, установив новый Event как новое значение.
        _navigateToMovieDetails.value = Event(id)
    }
}




