package com.profitmobile.valiev.cinemate.ui.movielist.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.profitmobile.valiev.cinemate.data.repository.MoviesRepository
import com.profitmobile.valiev.cinemate.utils.Event
import java.util.*
import javax.inject.Inject


/**
 * [ViewModel] для [SearchMoviesFragment].
 */
class SearchMoviesViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository
): ViewModel(){

    private val _query = MutableLiveData<String>()
    val query : LiveData<String> = _query

    private val _searchResult = Transformations
        .map(_query) { moviesRepository.searchMovies(it) }

    val pagedData = Transformations
        .switchMap(_searchResult) { it.pagedList }

    val networkState = Transformations
        .switchMap(_searchResult) { it.networkState }

    fun setQuery(originalInput: String) {
        val input = originalInput.toLowerCase(Locale.getDefault()).trim()
        if (input == _query.value) return
        _query.value = input
    }

    fun retry() {
        _searchResult.value?.retry?.invoke()
    }


    private val _navigateToMovieDetails = MutableLiveData<Event<Long?>>()
    val navigateToMovieDetails: LiveData<Event<Long?>>
        get() = _navigateToMovieDetails

    fun navigateToDetailsEvent(id: Long?) {

        _navigateToMovieDetails.value = Event(id)
    }
}