package com.profitmobile.valiev.cinemate.di

import com.profitmobile.valiev.cinemate.ui.moviedetails.DetailsActivity
import com.profitmobile.valiev.cinemate.ui.movielist.common.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Dagger module для добавления [MainActivity] к графу зависимостей.
 */
@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity
}

/**
 * Dagger module для добавления [DetailsActivity] к графу зависимостей.
 */
@Module
abstract class DetailsActivityModule {
    @ContributesAndroidInjector()
    abstract fun contributeDetailsActivity(): DetailsActivity
}

