package com.profitmobile.valiev.cinemate.ui.moviedetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.profitmobile.valiev.cinemate.data.db.Review
import com.profitmobile.valiev.cinemate.databinding.ItemReviewBinding


/**
 *  [ListAdapter] для [Review] list.
 */
class ReviewsAdapter: ListAdapter<Review, ReviewViewHolder>(ReviewDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}


/**
 *  ViewHolder  bind  [Review] item.
 */
class ReviewViewHolder (
    val binding: ItemReviewBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(review: Review) {
        binding.review = review
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ReviewViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemReviewBinding
                .inflate(inflater, parent, false)

            return ReviewViewHolder(binding)
        }
    }
}


/**
 * DiffCallback для вычисления разницы между двумя ненулевыми элементами в списке.
 *
 * Используется ListAdapter для расчета минимального количества изменений между
 * старый список и новый список, переданный в `submitList`.
 */
class ReviewDiffCallback : DiffUtil.ItemCallback<Review>() {
    override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
        return oldItem == newItem
    }
}