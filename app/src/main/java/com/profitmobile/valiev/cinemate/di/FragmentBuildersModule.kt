package com.profitmobile.valiev.cinemate.di

import com.profitmobile.valiev.cinemate.ui.movielist.common.MainActivity
import com.profitmobile.valiev.cinemate.ui.movielist.discover.FilteredMoviesFragment
import com.profitmobile.valiev.cinemate.ui.movielist.favorites.FavoriteMoviesFragment
import com.profitmobile.valiev.cinemate.ui.movielist.search.SearchMoviesFragment

import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Dagger module для фрагментов, размещенных на [MainActivity]
 */
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeTmdbMoviesFragment(): FilteredMoviesFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchMoviesFragment(): SearchMoviesFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoriteMoviesFragment(): FavoriteMoviesFragment
}