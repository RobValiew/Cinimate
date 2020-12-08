package com.profitmobile.valiev.cinemate.ui.movielist.common

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.profitmobile.valiev.cinemate.data.db.Movie


/**
 * DiffCallback для вычисления разницы между двумя ненулевыми элементами в списке. Использован
 * [ListAdapter] для расчета минимального количества изменений между старым списком и
 * и новый список, переданный в `submitList`.
 *
 * https://codelabs.developers.google.com/codelabs/kotlin-android-training-diffutil-databinding
 * https://developer.android.com/topic/libraries/architecture/paging/ui#implement-diffing-callback
 */
class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem == newItem
    }
}