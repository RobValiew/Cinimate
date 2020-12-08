package com.profitmobile.valiev.cinemate.ui.movielist.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.profitmobile.valiev.cinemate.data.repository.MoviesRepository
import com.profitmobile.valiev.cinemate.utils.Event
import javax.inject.Inject


/**
 *[ViewModel] для [FavoriteMoviesFragment].
 */
class FavoriteMoviesViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository
): ViewModel(){

    val favorites = moviesRepository.getAllFavoriteMovies()

    // Обрабатывает переход к деталям выбранного фильма.
    private val _navigateToMovieDetails = MutableLiveData<Event<Long?>>()
    val navigateToMovieDetails: LiveData<Event<Long?>>
        get() = _navigateToMovieDetails

    fun navigateToDetailsEvent(id: Long?) {
        //Запустить Event, установив новый Event как новое значение
        _navigateToMovieDetails.value = Event(id)
    }
}