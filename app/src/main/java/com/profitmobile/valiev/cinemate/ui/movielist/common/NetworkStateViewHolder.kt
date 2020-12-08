package com.profitmobile.valiev.cinemate.ui.movielist.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.profitmobile.valiev.cinemate.data.db.Movie
import com.profitmobile.valiev.cinemate.data.network.Resource
import com.profitmobile.valiev.cinemate.databinding.NetworkStateBinding


/**
 * ViewHolder которые могут отображать загрузку или иметь действие щелчка.
 * Используется для отображения сетевого состояния пейджинга.
 */
class NetworkStateViewHolder(
    private val binding: NetworkStateBinding,
    private val retryCallback: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup, retryCallback: () -> Unit): NetworkStateViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = NetworkStateBinding
                .inflate(inflater, parent, false)
            return NetworkStateViewHolder(
                binding,
                retryCallback
            )
        }
    }

    fun bind(networkState: Resource<PagedList<Movie>>?) {
        binding.resource = networkState
        binding.retryButton.setOnClickListener{ retryCallback() }
        binding.executePendingBindings()
    }
}
