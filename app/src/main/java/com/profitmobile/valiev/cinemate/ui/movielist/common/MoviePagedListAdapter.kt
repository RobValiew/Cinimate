package com.profitmobile.valiev.cinemate.ui.movielist.common

import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import com.profitmobile.valiev.cinemate.R
import com.profitmobile.valiev.cinemate.data.db.Movie
import com.profitmobile.valiev.cinemate.data.network.Resource
import com.profitmobile.valiev.cinemate.data.network.Status

/**
 * [PagedListAdapter] для элементов списка  [Movie].
 *
 * [PagedListAdapter] - это реализация [RecyclerView.Adapter], которая
 * представляет данные из [PagedList]. Этот адаптер слушает [PagedList]
 * загрузка обратных вызовов по мере загрузки страниц и использование [DiffUtil] в качестве фона
 * поток для вычисления точных обновлений по мере получения новых [PagedList].
 * Обрабатывает как внутреннюю подкачку списка по мере загрузки дополнительных данных, так и
 * обновления в виде новых PagedLists.
 *
 * https://developer.android.com/topic/libraries/architecture/paging/ui#implement-diffing-callback
 * https://github.com/android/android-architecture-components/tree/master/PagingWithNetworkSample
 */
class MoviePagedListAdapter (
    private val movieClickCallback: (id:Long?) -> Unit,
    private val retryCallback: () -> Unit
) : PagedListAdapter<Movie, RecyclerView.ViewHolder?>(MovieDiffCallback()) {

    private var resource: Resource<PagedList<Movie>>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_movie -> MovieViewHolder.create(
                parent,
                movieClickCallback
            )
            R.layout.network_state -> NetworkStateViewHolder.create(
                parent,
                retryCallback
            )
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MovieViewHolder -> holder.bind(getItem(position))
            is NetworkStateViewHolder -> holder.bind(resource)
            else -> throw IllegalArgumentException("Unknown view type " + getItemViewType(position))
        }
    }

    private fun hasExtraRow() = resource != null && resource?.status != Status.SUCCESS

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.network_state
        } else {
            R.layout.item_movie
        }
    }

    fun setNetworkState(newNetworkState: Resource<PagedList<Movie>>?) {
        val previousState = resource
        val hadExtraRow = hasExtraRow()
        resource = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }
}
