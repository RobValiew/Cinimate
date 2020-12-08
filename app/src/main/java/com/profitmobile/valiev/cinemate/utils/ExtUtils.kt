package com.profitmobile.valiev.cinemate.utils

import android.content.Context
import android.view.MenuItem
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.palette.graphics.Palette


fun MenuItem.tintMenuIcon(context: Context, @ColorRes color: Int) {
    // https://stackoverflow.com/questions/26780046/menuitem-tinting-on-appcompat-toolbar
    val iconWrapper = DrawableCompat.wrap(icon)
    DrawableCompat.setTint(iconWrapper, ContextCompat.getColor(context, color))
    icon = iconWrapper
}


fun Palette.getDominantColor(): Palette.Swatch? {
    // Извлекать цвета из изображения с помощью класса Platte.
    // https://developer.android.com/training/material/palette-colors
    var result = dominantSwatch
    if (vibrantSwatch != null) {
        result = vibrantSwatch
    } else if (mutedSwatch != null) {
        result = mutedSwatch
    }
    return result
}