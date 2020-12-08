package com.profitmobile.valiev.cinemate.di

import android.app.Application
import androidx.room.Room
import com.profitmobile.valiev.cinemate.data.db.MovieDao
import com.profitmobile.valiev.cinemate.data.db.MoviesDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Dagger module который предоставляет базу данных Room для этого приложения, и [MovieDao]
 */
@Module(includes = [ViewModelModule::class])
class RoomModule {

    @Singleton
    @Provides
    fun provideDatabase(app: Application): MoviesDatabase {
        return Room
            .databaseBuilder(app, MoviesDatabase::class.java, "movies")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideMovieDao(database: MoviesDatabase): MovieDao {
        return database.movieDao
    }
}