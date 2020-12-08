package com.profitmobile.valiev.cinemate.di

import com.profitmobile.valiev.cinemate.BuildConfig
import com.profitmobile.valiev.cinemate.data.network.TmdbApiService
import com.profitmobile.valiev.cinemate.utils.LiveDataCallAdapterFactory
import com.profitmobile.valiev.cinemate.utils.TMDB_BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

/**
 * Dagger module который предоставляет сервис TMDB API для этого приложения,
 * вместе с другими компонентами, связанными с REST API.
 */
@Module(includes = [ViewModelModule::class])
class RestModule {

    @Singleton
    @Provides
    fun provideAuthInterceptor() : Interceptor {
        // Поскольку запрашиваем API, который принимает ключ API в качестве параметра запроса,
        //  использою Interceptor, который добавит параметр запроса к каждому методу запроса.
        // https://futurestud.io/tutorials/retrofit-2-how-to-add-query-parameters-to-every-request
        return Interceptor { chain: Interceptor.Chain ->
            val initialRequest = chain.request()

            val url = initialRequest.url.newBuilder()
                .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                .build()

            val newRequest = initialRequest.newBuilder()
                .url(url)
                .build()

            chain.proceed(newRequest)
        }
    }

    @Singleton
    @Provides
    fun provideLoggingInterceptor() : HttpLoggingInterceptor {
        // полностью полагается на OkHttp для любых сетевых операций.
        // логировние по умолчанию больше не интегрировано в Retrofit 2,
        //  использовать Interceptor логов для OkHttp.
       return HttpLoggingInterceptor().apply {
            level = when {
                BuildConfig.DEBUG -> HttpLoggingInterceptor.Level.BODY
                else -> HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        authInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ) : OkHttpClient{
        // OkHttpClient logging, auth interceptors.
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideMoshi() : Moshi {
        // Создание объект Moshi, который будет использовать Retrofit,
        // чтобы добавить адаптер Kotlin для полной совместимости с Kotlin.
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        moshi: Moshi,
        okHttpClient : OkHttpClient
    ) : TmdbApiService {
        // Создаем объект Retrofit с помощью конвертера Moshi для анализа JSON и
        // LiveDataCallAdapter, который преобразует вызов модернизации в LiveData
        // ApiResponse.
       return Retrofit.Builder()
           .baseUrl(TMDB_BASE_URL)
           .addConverterFactory(MoshiConverterFactory.create(moshi))
           .addCallAdapterFactory(LiveDataCallAdapterFactory())
           .client(okHttpClient)
           .build()
           .create(TmdbApiService::class.java)
    }
}