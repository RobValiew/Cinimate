package com.profitmobile.valiev.cinemate.ui.moviedetails

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.profitmobile.valiev.cinemate.R
import com.profitmobile.valiev.cinemate.data.repository.MoviesRepository
import com.profitmobile.valiev.cinemate.utils.Event
import javax.inject.Inject


/**
 * [ViewModel] используется в[DetailsActivity].
 *Хранит и управляет данными, связанными с пользовательским интерфейсом, с учетом жизненного цикла.
 */
class DetailsViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository
): ViewModel() {

    private val _movieId = MutableLiveData<Long>()

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    val movieDetails = Transformations.switchMap(_movieId) {
            moviesRepository.loadMovieDetails(it)
        }

    var isFavorite = movieDetails.value?.data?.movie?.isFavorite

    fun setId(id: Long) {
        _movieId.value = id
    }

    fun updateFavorite(favorite: Boolean?) {
        isFavorite = favorite
    }

    // Обновить фильм в базе данных и обновить сообщение Snackbar.
    fun onFavoriteClicked() {
        isFavorite?.let {
            isFavorite = when {
                it -> {
                    moviesRepository.unfavorite(_movieId.value)
                    showSnackbarMessage(R.string.movie_removed_from_favorites)
                    false
                }
                else -> {
                    moviesRepository.favorite(_movieId.value)
                    showSnackbarMessage(R.string.movie_added_to_favorites)
                    true
                }
            }
        }
    }

    fun retry(id: Long) {
        _movieId.value = id
    }

    private fun showSnackbarMessage(@StringRes message: Int) {
        _snackbarText.value = Event(message)
    }
}