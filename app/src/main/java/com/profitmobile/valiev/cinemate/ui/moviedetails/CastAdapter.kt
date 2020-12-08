package com.profitmobile.valiev.cinemate.ui.moviedetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.profitmobile.valiev.cinemate.R
import com.profitmobile.valiev.cinemate.data.db.Cast
import com.profitmobile.valiev.cinemate.databinding.ItemCastBinding
import com.profitmobile.valiev.cinemate.utils.GlideApp
import com.profitmobile.valiev.cinemate.utils.IMAGE_BASE_URL
import com.profitmobile.valiev.cinemate.utils.IMAGE_SIZE_W185


/**
 * [ListAdapter]  [Cast] list.
 */
class CastAdapter: ListAdapter<Cast, CastViewHolder>(CastDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        return CastViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

/**
 * ViewHolder  bind  [Cast] item.
 */
class CastViewHolder (val binding: ItemCastBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(cast: Cast) {
        bindProfileImage(cast)
        binding.cast = cast
        binding.executePendingBindings()
    }

    private fun bindProfileImage(cast: Cast) {
        // Путь к изображению профиля может быть null проверка.
        cast.profileImagePath?.let {
            val profileImageUrl = IMAGE_BASE_URL + IMAGE_SIZE_W185 + cast.profileImagePath
            GlideApp.with(binding.root.context)
                .load(profileImageUrl)
                .placeholder(R.drawable.loading_animation)
                .error(R.color.imagePlaceholder)
                .fallback(R.color.imagePlaceholder)
                .into(binding.castItemProfileImage)
        }
    }

    companion object {
        fun from(parent: ViewGroup): CastViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemCastBinding
                .inflate(inflater, parent, false)

            return CastViewHolder(binding)
        }
    }
}

/**
 *DiffCallback для вычисления разницы между двумя ненулевыми элементами в списке.
 *
 * Используется ListAdapter для расчета минимального количества изменений между
 * старый список и новый список, переданный в `submitList`.
 */
class CastDiffCallback : DiffUtil.ItemCallback<Cast>() {
    override fun areItemsTheSame(oldItem: Cast, newItem: Cast): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Cast, newItem: Cast): Boolean {
        return oldItem == newItem
    }
}