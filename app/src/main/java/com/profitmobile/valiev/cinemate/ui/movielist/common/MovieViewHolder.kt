package com.profitmobile.valiev.cinemate.ui.movielist.common

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.profitmobile.valiev.cinemate.R
import com.profitmobile.valiev.cinemate.data.db.Movie
import com.profitmobile.valiev.cinemate.databinding.ItemMovieBinding
import com.profitmobile.valiev.cinemate.utils.GlideApp
import com.profitmobile.valiev.cinemate.utils.IMAGE_URL
import com.profitmobile.valiev.cinemate.utils.getDominantColor
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import timber.log.Timber

/**
 *  [RecyclerView.ViewHolder] который может привязать элемент [Movie]. Он также принимает
 * нулевые элементы, поскольку данные могли не быть получены до привязки.
 */
class MovieViewHolder private constructor(
    val binding: ItemMovieBinding,
    val movieClickCallback: (id:Long?) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup, movieClickCallback: (id: Long?) -> Unit): MovieViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemMovieBinding.inflate(inflater, parent, false)
            return MovieViewHolder(
                binding,
                movieClickCallback
            )
        }
    }

    /**
     * Элементы могут иметь значение NULL, если они еще не загружены. PagedListAdapter
     * повторно привяжет ViewHolder при загрузке фильма.
     */
    fun bind(movie: Movie?) {
        binding.movie = movie
        bindMoviePoster(movie)
        binding.movieItemCardView.setOnClickListener{
            movieClickCallback(movie?.id)
        }
        binding.executePendingBindings()
    }

    private fun bindMoviePoster(movie: Movie?) {
        GlideApp.with(binding.root.context)
            // Вызов Glide.with () возвращает RequestBuilder.
            // По умолчанию вы получаете RequestBuilder <Drawable>, но
            // вы можете изменить запрошенный тип, используя методы as ...
            // Например, asBitmap () возвращает RequestListener <Bitmap>.
            .asBitmap()
            .load(IMAGE_URL + movie?.posterPath)
            // Отображать placeholder, пока изображение не будет загружено и обработано.
            .placeholder(R.drawable.loading_animation)
            //Указать ошибку placeholder когда Glide не может загрузить
            // изображение. Это будет показано для несуществующего URL.
            .error(R.color.imagePlaceholder)
            // Используйте резервный ресурс изображения, если URL-адрес может быть нулевым.
            .fallback(R.color.imagePlaceholder)
            // Следите за ошибками и успешной загрузкой изображений.
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    exception: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean): Boolean {
                    Timber.d("Image loading failed: %s", exception?.message)
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    resource?.let { generatePaletteAsync(bitmap = it) }
                    return false
                }
            })
            .into(binding.movieItemPoster)
    }

    private fun generatePaletteAsync(bitmap: Bitmap) {
        // Извлечение  цветов из изображения с помощью Platte.
        // https://developer.android.com/training/material/palette-colors
        Palette.from(bitmap).generate { palette ->
            val swatch = palette?.let { palette.getDominantColor() }
            swatch?.let {
                binding.movieItemCardView.setCardBackgroundColor(swatch.rgb)
                binding.movieItemCardView.strokeColor = swatch.rgb
            }
        }
    }
}