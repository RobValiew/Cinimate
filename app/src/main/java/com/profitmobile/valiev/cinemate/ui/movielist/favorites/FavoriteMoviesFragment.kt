package com.profitmobile.valiev.cinemate.ui.movielist.favorites


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager

import com.profitmobile.valiev.cinemate.R
import com.profitmobile.valiev.cinemate.databinding.FragmentFavoriteMoviesBinding
import com.profitmobile.valiev.cinemate.ui.moviedetails.DetailsActivity
import com.profitmobile.valiev.cinemate.ui.moviedetails.EXTRA_MOVIE_ID
import com.profitmobile.valiev.cinemate.utils.EventObserver
import com.profitmobile.valiev.cinemate.utils.GridMarginDecoration
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


/**
 * [Fragment] который отображает список любимых фильмов.
 */
class FavoriteMoviesFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val favoritesViewModel: FavoriteMoviesViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var binding : FragmentFavoriteMoviesBinding
    private lateinit var favoritesAdapter : FavoriteMoviesAdapter

    override fun onAttach(context: Context) {
        // Dagger Fragments, inject
        // предотвращает несоответствия при повторном присоединении Фрагмента.
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteMoviesBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.favoritesViewModel = favoritesViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        setToolbarTitle()
        initFavoritesAdapter()
        navigateToDetails()
    }

    private fun setToolbarTitle(){
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.favorites)
    }

    private fun initFavoritesAdapter() {
        binding.rvFavoritesList.layoutManager = GridLayoutManager(
            activity, resources.getInteger(R.integer.span_count))
        binding.rvFavoritesList.addItemDecoration(
            GridMarginDecoration(requireContext(),R.dimen.grid_item_spacing)
        )

        favoritesAdapter = FavoriteMoviesAdapter {
            favoritesViewModel.navigateToDetailsEvent(it)
        }
        binding.rvFavoritesList.adapter = favoritesAdapter
    }

    private fun navigateToDetails(){
        favoritesViewModel.navigateToMovieDetails.observe(viewLifecycleOwner, EventObserver {
            val intent = Intent(requireContext(), DetailsActivity::class.java)
            intent.putExtra(EXTRA_MOVIE_ID, it)
            startActivity(intent)
        })
    }
}
