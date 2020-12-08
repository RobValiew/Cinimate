package com.profitmobile.valiev.cinemate.ui.moviedetails

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.profitmobile.valiev.cinemate.R
import com.profitmobile.valiev.cinemate.data.db.MovieDetails
import com.profitmobile.valiev.cinemate.databinding.ActivityDetailsBinding
import com.profitmobile.valiev.cinemate.utils.EventObserver
import com.profitmobile.valiev.cinemate.utils.tintMenuIcon
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import javax.inject.Inject


const val EXTRA_MOVIE_ID = "EXTRA_MOVIE_ID"
const val INVALID_MOVIE_ID = -1


/**
 *  UI Controller для отображения деталей фильма, загруженного из The Movie DB.
 */
class DetailsActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val detailsViewModel: DetailsViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // При использовании Dagger с активностями делать инъекцию как можно раньше.
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        // Inflate layout и получить экземпляр класса привязки.
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details)

        //Разрешить привязку данных observe live data к жизненному циклу activity.
        binding.lifecycleOwner = this

        // Получить идентификатор фильма, переданный как intent extra.
        val movieId = intent.getLongExtra(EXTRA_MOVIE_ID, INVALID_MOVIE_ID.toLong())
        if (movieId == INVALID_MOVIE_ID.toLong()) closeOnError()

        handleUpNavigation()
        setupAdapters()
        populateUi(movieId)
    }

    private fun setupAdapters() {
        binding.contentDetails.rvCast.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = CastAdapter()
        }

        binding.contentDetails.rvTrailers.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.contentDetails.rvTrailers.adapter = TrailerAdapter()
        }

        binding.contentDetails.rvReviews.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ReviewsAdapter()
        }
    }

    private fun populateUi(movieId: Long) {
        detailsViewModel.setId(movieId)
        detailsViewModel.movieDetails.observe(this, Observer { resource ->
            // Set переменные привязки
            binding.resource = resource
            binding.movieDetails = resource.data

            // toolbar title
            setToolbarTitleIfCollapsed(resource.data)

            // favorites
            detailsViewModel.updateFavorite(resource.data?.movie?.isFavorite)

            // Примечание: при изменении деталей фильма меняется и содержимое меню,
            // так что меню надо перерисовать.
            invalidateOptionsMenu()
        })

        // Observe Snackbar
        detailsViewModel.snackbarText.observe(this, EventObserver { message ->
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        })

        // retries
        binding.networkState.retryButton.setOnClickListener {
            detailsViewModel.retry(movieId)
        }
    }

    private fun handleUpNavigation() {
        setSupportActionBar(binding.detailsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setToolbarTitleIfCollapsed(
        movieDetails: MovieDetails?
    ) {
        // set toolbar title только когда панель инструментов свернута,
        //нужно добавить OnOffsetChangedListener к AppBarLayout определить
        //когда CollapsingToolbarLayout свернут или развернут.
        binding.detailsAppBar.addOnOffsetChangedListener(object : OnOffsetChangedListener {
            var isShown = true
            var totalScrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                // Инициируем общий диапазон прокрутки
                if (totalScrollRange == -1) {
                    totalScrollRange = appBarLayout.totalScrollRange
                }
                // если toolbar полностью убран, установить заголовок сворачивающейся панели.
                if (totalScrollRange + verticalOffset == 0) {
                    binding.detailsCollapsingToolbar.title = movieDetails?.movie?.title
                    isShown = true
                } else if (isShown) {
                    // Когда панель инструментов развернута, отобразить пустую строку.
                    binding.detailsCollapsingToolbar.title = " "
                    isShown = false
                }
            }
        })
    }

    private fun closeOnError() {
        finish()
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.movie_details_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let { setFavoriteIcon(it) }
        return true
    }

    private fun setFavoriteIcon(menu: Menu) {
        val favoriteItem = menu.findItem(R.id.action_add_remove_favorite)
        detailsViewModel.isFavorite?.let {
            when {
                it -> {
                    favoriteItem.setIcon(R.drawable.ic_favorite_black_24dp)
                        .setTitle(R.string.action_remove_from_favorites)
                }
                else -> {
                    favoriteItem.setIcon(R.drawable.ic_favorite_border_black_24dp)
                        .setTitle(R.string.action_add_to_favorites)
                }
            }
        }

        favoriteItem.tintMenuIcon(this, android.R.color.white)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                true
            }

            R.id.action_add_remove_favorite -> {
                detailsViewModel.onFavoriteClicked()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
