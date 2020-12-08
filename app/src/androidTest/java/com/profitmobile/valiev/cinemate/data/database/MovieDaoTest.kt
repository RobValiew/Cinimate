package com.profitmobile.valiev.cinemate.data.database

import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.profitmobile.valiev.cinemate.data.db.*
import com.profitmobile.valiev.cinemate.util.PojosTestUtil
import com.profitmobile.valiev.cinemate.util.getOrAwaitValue
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 * Test  [MovieDao].
 */
@RunWith(AndroidJUnit4::class)
class MovieDaoTest {

    private lateinit var database: MoviesDatabase

    // Меняет местами background executor, используемый Компонентами архитектуры, на
    // другой, который выполняет каждую задачу синхронно.
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val countingTaskExecutorRule = CountingTaskExecutorRule()

    @Before
    fun initDb() {

        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MoviesDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() {
        countingTaskExecutorRule.drainTasks(10, TimeUnit.SECONDS)
        database.close()
    }

    @Test
    fun insertGenres_getsCorrectGenresList() {

        val movieId = 0L
        val fooGenre = Genre(0L, movieId, "foo")
        val barGenre = Genre(1L, movieId, "bar")

        database.movieDao.insertGenres(listOf(fooGenre,barGenre))

        val loadedGenres = database.movieDao.getGenres(movieId).getOrAwaitValue()

        assertThat(loadedGenres, notNullValue())
        assertThat(loadedGenres.size, `is`(2))

        val first = loadedGenres[0]
        assertThat(first.id, `is`(0L))
        assertThat(first.movieId, `is` (movieId))
        assertThat(first.name, `is`("foo"))

        val second = loadedGenres[1]
        assertThat(second.id, `is`(1L))
        assertThat(second.movieId, `is` (movieId))
        assertThat(second.name, `is`("bar"))
    }

    @Test
    fun insertGenres_thenUpdateMovie() {
        val movieId = 1L
        val movie = PojosTestUtil.createGenericMovie(movieId)
        val genre = Genre(1L, movie.id, "foo")
        database.runInTransaction {
            database.movieDao.insertMovie(movie)
            database.movieDao.insertGenres(listOf(genre))
        }

        var loadedGenres = database.movieDao.getGenres(movie.id).getOrAwaitValue()
        assertThat(loadedGenres.size, `is`(1))

        val newMovie = PojosTestUtil.createGenericMovie(movieId)
        database.movieDao.insertMovie(newMovie)
        loadedGenres = database.movieDao.getGenres(movie.id).getOrAwaitValue()
        assertThat(loadedGenres.size, `is`(1))
    }

    @Test
    fun insertCast_getsCorrectCastList() {

        val movieId = 2L
        val fooCast = Cast(0L, movieId, "fooName", "fooPath")
        val barCast = Cast(1L, movieId, "barName", "barPath")

        database.movieDao.insertCast(listOf(fooCast, barCast))

        val loadedCast = database.movieDao.getCast(movieId).getOrAwaitValue()

        assertThat(loadedCast, notNullValue())
        assertThat(loadedCast.size, `is`(2))

        val first = loadedCast[0]
        assertThat(first.id, `is`(0L))
        assertThat(first.movieId, `is` (movieId))
        assertThat(first.actorName, `is`("fooName"))
        assertThat(first.profileImagePath, `is`("fooPath"))

        val second = loadedCast[1]
        assertThat(second.id, `is`(1L))
        assertThat(second.movieId, `is` (movieId))
        assertThat(second.actorName, `is`("barName"))
        assertThat(second.profileImagePath, `is`("barPath"))
    }

    @Test
    fun insertReviews_getsCorrectReviewsList() {
        val movieId = 3L
        val fooReview = Review(
            "fooId", movieId, "fooAuthor", "fooContent", "fooUrl"
        )
        val barReview = Review(
            "barId", movieId, "barAuthor", "barContent", "barUrl"
        )

        database.movieDao.insertReviews(listOf(fooReview, barReview))

        val loadedReviews = database.movieDao.getReviews(movieId).getOrAwaitValue()

        assertThat(loadedReviews, notNullValue())
        assertThat(loadedReviews.size, `is`(2))

        val first = loadedReviews[0]
        assertThat(first.id, `is`("fooId"))
        assertThat(first.movieId, `is` (movieId))
        assertThat(first.author, `is`("fooAuthor"))
        assertThat(first.content, `is`("fooContent"))
        assertThat(first.url, `is`("fooUrl"))

        val second = loadedReviews[1]
        assertThat(second.id, `is`("barId"))
        assertThat(second.movieId, `is` (movieId))
        assertThat(second.author, `is`("barAuthor"))
        assertThat(second.content, `is`("barContent"))
        assertThat(second.url, `is`("barUrl"))
    }

    @Test
    fun insertTrailers_getsCorrectTrailersList() {

        val movieId = 4L
        val fooTrailer = Trailer("fooId", movieId, "fooKey", "fooName")
        val barTrailer = Trailer("barId", movieId, "barKey", "barName")

        database.movieDao.insertTrailers(listOf(fooTrailer, barTrailer))

        val loadedTrailers = database.movieDao.getTrailers(movieId).getOrAwaitValue()

        assertThat(loadedTrailers, notNullValue())
        assertThat(loadedTrailers.size, `is`(2))

        val first = loadedTrailers[0]
        assertThat(first.id, `is`("fooId"))
        assertThat(first.movieId, `is` (movieId))
        assertThat(first.key, `is`("fooKey"))
        assertThat(first.name, `is`("fooName"))

        val second = loadedTrailers[1]
        assertThat(second.id, `is`("barId"))
        assertThat(second.movieId, `is` (movieId))
        assertThat(second.key, `is`("barKey"))
        assertThat(second.name, `is`("barName"))
    }
}