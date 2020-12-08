package com.profitmobile.valiev.cinemate.data.db

import androidx.room.*

/**
 *  [Room] database
 */
@Database(
    entities = [Movie::class,  Genre:: class, Cast::class, Trailer::class, Review::class],
    version = 21,
    exportSchema = false
)
abstract class MoviesDatabase : RoomDatabase() {
    abstract val movieDao: MovieDao
}




