package com.profitmobile.valiev.cinemate.utils

import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

/**
 * Чтобы использовать библиотеки интеграции и / или расширения API Glide,
 * добавить ровно одну реализацию AppGlideModule и аннотировать ее
 * Аннотация @GlideModule.
 *
 * https://bumptech.github.io/glide/doc/configuration.html#applications
 */
@GlideModule
class MovieAppGlideModule : AppGlideModule()