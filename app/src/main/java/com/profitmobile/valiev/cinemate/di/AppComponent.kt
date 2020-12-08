package com.profitmobile.valiev.cinemate.di

import android.app.Application
import com.profitmobile.valiev.cinemate.MovieApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

/**
* Определение компонента Dagger, который добавляет информацию из различных модулей.
* Классы, помеченные @Singleton, будут иметь единственный экземпляр в этом Компоненте.
*/
@Singleton
@Component(modules = [
        AndroidInjectionModule::class,
        RestModule::class,
        RoomModule::class,
        MainActivityModule::class,
        DetailsActivityModule::class]
)
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(movieApplication: MovieApplication)
}