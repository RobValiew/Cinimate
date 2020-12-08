package com.profitmobile.valiev.cinemate.ui.movielist.discover


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.profitmobile.valiev.cinemate.R
import com.profitmobile.valiev.cinemate.data.network.Status

import com.profitmobile.valiev.cinemate.databinding.FragmentFilteredMoviesBinding
import com.profitmobile.valiev.cinemate.ui.moviedetails.DetailsActivity
import com.profitmobile.valiev.cinemate.ui.moviedetails.EXTRA_MOVIE_ID
import com.profitmobile.valiev.cinemate.ui.movielist.common.MoviePagedListAdapter
import com.profitmobile.valiev.cinemate.utils.EventObserver
import com.profitmobile.valiev.cinemate.utils.GridMarginDecoration
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * [Fragment] который отображает список фильмов TMDB.
 */
class FilteredMoviesFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val moviesViewModel: FilteredMoviesViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var binding: FragmentFilteredMoviesBinding
    private lateinit var movieAdapter: MoviePagedListAdapter

    override fun onAttach(context: Context) {
        // Dagger Fragment иньекция
        // Это предотвращает несоответствия при повторном присоединении Фрагмента.
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate layout для fragment и получить экземпляр класса привязки.
        binding = FragmentFilteredMoviesBinding.inflate(inflater)

        // data binding  observe LiveData с жизненным циклом этого фрагмента.
        binding.lifecycleOwner = this

        // Сообщите, что этот фрагмент хотел бы заполнить меню параметров.
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        initMoviesAdapter()
        populateUi()
        initSwipeToRefresh()
        navigateToDetails()
    }

    private fun initMoviesAdapter() {
        // Подключить movie list adapter с recycler view.
        movieAdapter = MoviePagedListAdapter(
            movieClickCallback = { moviesViewModel.navigateToDetailsEvent(it) },
            retryCallback = { moviesViewModel.retry() }
        )

        binding.rvMovieList.adapter = movieAdapter
        binding.rvMovieList.addItemDecoration(
            GridMarginDecoration(requireContext(),R.dimen.grid_item_spacing)
        )

        // Создать индивидуальный GridLayoutManager что позволяет разный диапазон
        // считается для разных строк. Это позволяет отображать сеть
        // сообщения о статусе и ошибках по всей строке (т.е.3 промежутка)
        val manager = GridLayoutManager(activity, resources.getInteger(R.integer.span_count))
        binding.rvMovieList.layoutManager = manager
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) =
                when (movieAdapter.getItemViewType(position)) {
                    R.layout.network_state -> manager.spanCount
                    else -> 1
                }
        }
    }

    private fun populateUi() {
        // Observe  movie list.
        moviesViewModel.pagedData.observe(viewLifecycleOwner, Observer {
            movieAdapter.submitList(it)
        })

        // Observe network
        moviesViewModel.networkState.observe(viewLifecycleOwner, Observer {
            movieAdapter.setNetworkState(it)
        })

        // Observe app bar title.
        moviesViewModel.title.observe(viewLifecycleOwner, Observer {
            (activity as AppCompatActivity).supportActionBar?.title = getString(it)
        })
    }

    private fun initSwipeToRefresh() {
        moviesViewModel.refreshState.observe(viewLifecycleOwner, Observer {
            binding.swipeRefresh.isRefreshing = it.status == Status.LOADING
        })

        binding.swipeRefresh.setOnRefreshListener {
            moviesViewModel.refresh()
        }
    }

    private fun navigateToDetails(){
        moviesViewModel.navigateToMovieDetails.observe(viewLifecycleOwner, EventObserver {
            val intent = Intent(requireContext(), DetailsActivity::class.java)
            intent.putExtra(EXTRA_MOVIE_ID, it)
            startActivity(intent)
        })
    }

    /**
     * Расширяет меню, содержащее параметры фильтрации.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.movies_filter_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Обновляет фильтр в [FilteredMoviesViewModel], когда элементы меню
     * выбираются из дополнительного меню.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        moviesViewModel.updateFilter(
            when (item.itemId) {
                R.id.category_now_playing -> MoviesFilter.NOW_PLAYING
                R.id.category_upcoming -> MoviesFilter.UPCOMING
                R.id.category_top_rated -> MoviesFilter.TOP_RATED
                else -> MoviesFilter.POPULAR
            }
        )

        // Вернуть true, так как меню было обработано
        return true
    }
}
