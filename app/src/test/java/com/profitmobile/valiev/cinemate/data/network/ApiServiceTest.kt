package com.profitmobile.valiev.cinemate.data.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.profitmobile.valiev.cinemate.util.LiveDataTestUtil.getValue
import com.profitmobile.valiev.cinemate.utils.LiveDataCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsNull.notNullValue
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@RunWith(JUnit4::class)
class ApiServiceTest {

    private lateinit var apiService: TmdbApiService

    private lateinit var mockWebServer: MockWebServer

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createService() {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        mockWebServer = MockWebServer()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(TmdbApiService::class.java)
    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun getMovieDetails() {
        enqueueResponse("movie_details_godfather.json")
        val response =
            (getValue(apiService.getAllMovieDetails(238)) as ApiSuccessResponse).body

        // Verify endpoint
        val request = mockWebServer.takeRequest()
        assertThat(request.path, `is`("/movie/238?append_to_response=videos,credits,reviews"))

        val movie = response.asMovie()
        val genres = response.asGenres()
        val cast = response.asCast()
        val trailers = response.asVideos()
        val reviews = response.asReviews()

        assertThat(movie, notNullValue())
        assertThat(movie.title, `is`("The Godfather"))
        assertThat(movie.overview, `is`("Spanning the years 1945 to 1955, a chronicle of the fictional Italian-American Corleone crime family. When organized crime family patriarch, Vito Corleone barely survives an attempt on his life, his youngest son, Michael steps in to take care of the would-be killers, launching a campaign of bloody revenge."))
        assertThat(movie.releaseDate, `is`("1972-03-14"))
        assertThat(movie.backdropPath, `is`("/6xKCYgH16UuwEGAyroLU6p8HLIn.jpg"))
        assertThat(movie.posterPath, `is`("/d4KNaTrltq6bpkFS01pYtyXa09m.jpg"))
        assertThat(movie.popularity, `is`(31.9))
        assertThat(movie.voteAverage, `is`(8.7))
        assertThat(movie.voteCount, `is`(11450))

        assertThat(genres, notNullValue())
        assertThat(genres.size, `is`(2))
        assertThat(genres[0].name, `is`("Drama"))
        assertThat(genres[1].name, `is`("Crime"))

        assertThat(cast, notNullValue())
        assertThat(cast.size, `is`(59))
        assertThat(cast[0].actorName, `is`("Marlon Brando"))

        assertThat(trailers, notNullValue())
        assertThat(trailers.size, `is`(2))
        assertThat(trailers[0].name, `is`("The Godfather- Offer He Can't Refuse"))
        assertThat(trailers[0].key, `is`("fBNpSRtfIUA"))
        assertThat(trailers[1].name, `is`("The Godfather (1972) Trailer #1 | Movieclips Classic Trailers"))
        assertThat(trailers[1].key, `is`("1x0GpEZnwa8"))

        assertThat(reviews, notNullValue())
        assertThat(reviews.size, `is`(1))
        val review = reviews[0]
        assertThat(review.id, `is`("5346fa840e0a265ffa001e20"))
        assertThat(review.author, `is`("futuretv"))
        assertThat(review.url, `is`("https://www.themoviedb.org/review/5346fa840e0a265ffa001e20"))
    }

    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val inputStream = javaClass.classLoader?.getResourceAsStream("api-response/$fileName")
        val source = inputStream?.source()?.buffer()
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        if (source != null) {
            mockWebServer.enqueue(
                mockResponse.setBody(source.readString(Charsets.UTF_8))
            )
        }
    }
}