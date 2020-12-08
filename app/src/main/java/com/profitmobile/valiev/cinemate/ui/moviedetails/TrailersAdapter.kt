package com.profitmobile.valiev.cinemate.ui.moviedetails

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.profitmobile.valiev.cinemate.R
import com.profitmobile.valiev.cinemate.data.db.Trailer
import com.profitmobile.valiev.cinemate.databinding.ItemTrailerBinding
import com.profitmobile.valiev.cinemate.utils.*


/**
 * [ListAdapter] для [Trailer] list.
 */
class TrailerAdapter: ListAdapter<Trailer, TrailerViewHolder>(TrailerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailerViewHolder {
        return TrailerViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TrailerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

/**
 * ViewHolder bind  [Trailer] item.
 */
class TrailerViewHolder (
    val binding: ItemTrailerBinding,
    val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(trailer: Trailer) {
        bindThumbnail(trailer)
        binding.trailerName.text = trailer.name
        binding.trailerCardView.setOnClickListener {
            playVideo(trailer)
        }
        binding.executePendingBindings()
    }

    private fun bindThumbnail(trailer: Trailer) {
        // https://stackoverflow.com/questions/2068344/how-do-i-get-a-youtube-video-thumbnail-from-the-youtube-api
        val thumbnail: String = YOUTUBE_TRAILER_THUMBNAIL_BASE_URL +
                trailer.key + YOUTUBE_TRAILER_THUMBNAIL_HQ
        GlideApp.with(binding.root.context)
            .load(thumbnail)
            .placeholder(R.drawable.loading_animation)
            .error(R.color.imagePlaceholder)
            .fallback(R.color.imagePlaceholder)
            .into(binding.trailerThumbnail)
    }

    private fun playVideo(trailer: Trailer) {
        // https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
        val appIntent  = Intent(Intent.ACTION_VIEW,
            Uri.parse(YOUTUBE_APP_BASE_URL + trailer.key))
        val webIntent = Intent(Intent.ACTION_VIEW,
            Uri.parse(YOUTUBE_WEB_BASE_URL + trailer.key))
        if (appIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(appIntent)
        } else {
            context.startActivity(webIntent)
        }
    }

    companion object {
        fun from(parent: ViewGroup): TrailerViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemTrailerBinding
                .inflate(inflater, parent, false)

            return TrailerViewHolder(binding, parent.context)
        }
    }
}

/**
 * DiffCallback для вычисления разницы между двумя ненулевыми элементами в списке.
 *
 * Используется ListAdapter для расчета минимального количества изменений между
 * старый список и новый список, переданный в `submitList`.
 */
class TrailerDiffCallback : DiffUtil.ItemCallback<Trailer>() {
    override fun areItemsTheSame(oldItem: Trailer, newItem: Trailer): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Trailer, newItem: Trailer): Boolean {
        return oldItem == newItem
    }
}