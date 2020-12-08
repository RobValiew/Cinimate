package com.profitmobile.valiev.cinemate.ui.movielist.favorites

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.profitmobile.valiev.cinemate.data.db.Movie
import com.profitmobile.valiev.cinemate.ui.movielist.common.MovieDiffCallback
import com.profitmobile.valiev.cinemate.ui.movielist.common.MovieViewHolder

/**
 * [ListAdapter] для favorite [Movie] list.
 */
class FavoriteMoviesAdapter(
    private val movieClickCallback: (id:Long?) -> Unit
) : ListAdapter<Movie, MovieViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder.create(parent, movieClickCallback)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

