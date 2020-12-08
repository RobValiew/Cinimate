package com.profitmobile.valiev.cinemate.utils

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


/**
 * Пользовательский ItemDecoration, обеспечивающий равный интервал между столбцами для RecyclerView, который размещает элементы в сетке.
 * см: https://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing
 */
class GridMarginDecoration private constructor(private val itemOffset: Int) : ItemDecoration() {

    constructor(context: Context, @DimenRes itemOffsetId: Int) :
            this(context.resources.getDimensionPixelSize(itemOffsetId))

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect[itemOffset, itemOffset, itemOffset] = itemOffset
    }
}