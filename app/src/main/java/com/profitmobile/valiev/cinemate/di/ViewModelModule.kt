package com.profitmobile.valiev.cinemate.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.profitmobile.valiev.cinemate.ui.moviedetails.DetailsViewModel
import com.profitmobile.valiev.cinemate.ui.movielist.discover.FilteredMoviesViewModel
import com.profitmobile.valiev.cinemate.ui.movielist.favorites.FavoriteMoviesViewModel
import com.profitmobile.valiev.cinemate.ui.movielist.search.SearchMoviesViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Dagger module который предоставляет модели представлений для этого приложения и поддерживаемый Dagger2
 * фабрика по их созданию.
 *
 * Примечание: Dagger позволяет связывать несколько объектов в коллекцию, даже если объекты
 * связаны в разных модулях с помощью мульти-привязок. Здесь мы используем мульти-привязку Map.
 *
 * См. Https://dagger.dev/multibindings.html.
 */
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(FilteredMoviesViewModel::class)
    abstract fun bindTmdbMoviesViewModel(filteredMoviesViewModel: FilteredMoviesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchMoviesViewModel::class)
    abstract fun bindSearchMoviesViewModel(searchMoviesViewModel: SearchMoviesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavoriteMoviesViewModel::class)
    abstract fun bindFavoriteMoviesViewModel(favoriteMoviesViewModel: FavoriteMoviesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DetailsViewModel::class)
    abstract fun bindDetailsViewModel(detailsViewModel: DetailsViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
