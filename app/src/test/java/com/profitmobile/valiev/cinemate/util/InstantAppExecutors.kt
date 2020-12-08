package com.profitmobile.valiev.cinemate.util

import com.profitmobile.valiev.cinemate.utils.AppExecutors
import java.util.concurrent.Executor

class InstantAppExecutors : AppExecutors(instant, instant, instant) {
    companion object {
        private val instant = Executor { it.run() }
    }
}