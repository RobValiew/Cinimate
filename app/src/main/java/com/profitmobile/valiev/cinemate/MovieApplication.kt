package com.profitmobile.valiev.cinemate

import android.app.Activity
import android.app.Application
import com.profitmobile.valiev.cinemate.di.DaggerAppComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

/**
 * При использовании Dagger для инъекции объектов Activity необходимо сделать свою
 * Реализацию приложения HasAndroidInjector. см: https://dagger.dev/android.html.
 */
class MovieApplication: Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector() = dispatchingAndroidInjector

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        DaggerAppComponent
            .builder()
            .application(this)
            .build()
            .inject(this)
    }
}