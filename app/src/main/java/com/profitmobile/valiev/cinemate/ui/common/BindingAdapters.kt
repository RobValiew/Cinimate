package com.profitmobile.valiev.cinemate.ui.common


import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.profitmobile.valiev.cinemate.R
import com.profitmobile.valiev.cinemate.data.db.*
import com.profitmobile.valiev.cinemate.ui.moviedetails.CastAdapter
import com.profitmobile.valiev.cinemate.ui.moviedetails.ReviewsAdapter
import com.profitmobile.valiev.cinemate.ui.moviedetails.TrailerAdapter
import com.profitmobile.valiev.cinemate.ui.movielist.favorites.FavoriteMoviesAdapter
import com.profitmobile.valiev.cinemate.utils.BACKDROP_URL
import com.profitmobile.valiev.cinemate.utils.GlideApp
import com.profitmobile.valiev.cinemate.utils.IMAGE_URL
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

/**
 * [BindingAdapter] который переключает видимость [View] между visible и gone.
 */
@BindingAdapter("app:visibleGone")
fun showHide(view: View, show: Boolean) {
    view.visibility = if (show) View.VISIBLE else View.GONE
}

/**
 * [BindingAdapter] использование библиотеки Glide для загрузки постеров
 */
@BindingAdapter("app:posterPath")
fun bindPosterImage(imageView: ImageView, posterPath: String?) {
    posterPath?.let {
        GlideApp.with(imageView.context)
            .load(IMAGE_URL + posterPath)
            .placeholder(R.drawable.loading_animation)
            .error(R.color.imagePlaceholder)
            .into(imageView)
    }
}

/**
 * [BindingAdapter]  использование библиотеки Glide для загрузки фонового изображения.
 */
@BindingAdapter("app:backdropPath")
fun bindBackdropImage(imageView: ImageView, backdropPath: String?) {
    backdropPath?.let {
        GlideApp.with(imageView.context)
            .load(BACKDROP_URL + backdropPath)
            .placeholder(R.color.colorPrimary)
            .error(R.color.colorPrimary)
            .into(imageView)
    }
}

/**
 * [BindingAdapter] который динамически создает [Chip] s для списка [Genre].
 */
@BindingAdapter("app:chipGroupItems")
fun bindGenreChipGroup(chipGroup: ChipGroup, genres: List<Genre>?) {
    // Поскольку мы используем  live data, любые изменения, которые вызовут
    // observer также вызовет повторную привязку данных, что может привести к дублированию.
    if (genres == null || chipGroup.childCount > 0) return

    // Динамическое создание chips(жанры). Примечание, необходимо обновить тему приложения.
    // унаследовать от Theme.MaterialComponents (или потомка).
    // https://stackoverflow.com/questions/55350567/dynamically-create-choice-chip-in-android
    for (genre in genres) {
        // Инициализировать новый экземпляр chip
        val chip = Chip(chipGroup.context)
        // Set chip text.
        chip.text = genre.name
        // Set chips ширина штриха.
        chip.chipStrokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 1f,
            chipGroup.context.resources.displayMetrics)
        // Set chip цвет обводки и цвет фона.
        chip.setChipStrokeColorResource(android.R.color.black)
        chip.setChipBackgroundColorResource(android.R.color.transparent)
        // добавить  chip в группу чипов.
        chipGroup.addView(chip)
    }
}

/**
 * [BindingAdapter] [Cast] list.
 */
@BindingAdapter("app:cast")
fun setCastItems(recyclerView: RecyclerView, items: List<Cast>?) {
    items?.let {
        (recyclerView.adapter as CastAdapter).submitList(items)
    }
}

/**
 * [BindingAdapter]  [Trailer] list.
 */
@BindingAdapter("app:trailers")
fun setTrailerItems(recyclerView: RecyclerView, items: List<Trailer>?) {
    items?.let {
        (recyclerView.adapter as TrailerAdapter).submitList(items)
    }
}

/**
 * [BindingAdapter]  [Review] list.
 */
@BindingAdapter("app:reviews")
fun setReviewItems(recyclerView: RecyclerView, items: List<Review>?) {
    items?.let {
        (recyclerView.adapter as ReviewsAdapter).submitList(items)
    }
}

/**
 * [BindingAdapter]  favorite [Movie] list.
 */
@BindingAdapter("app:favoriteMovies")
fun setFavoriteMovies(recyclerView: RecyclerView, items: List<Movie>?) {
    items?.let {
        (recyclerView.adapter as FavoriteMoviesAdapter).submitList(items)
    }
}



