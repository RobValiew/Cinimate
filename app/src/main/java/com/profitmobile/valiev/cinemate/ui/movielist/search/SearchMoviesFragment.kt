package com.profitmobile.valiev.cinemate.ui.movielist.search


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager

import com.profitmobile.valiev.cinemate.R
import com.profitmobile.valiev.cinemate.databinding.FragmentSearchMoviesBinding
import com.profitmobile.valiev.cinemate.ui.moviedetails.DetailsActivity
import com.profitmobile.valiev.cinemate.ui.moviedetails.EXTRA_MOVIE_ID
import com.profitmobile.valiev.cinemate.ui.movielist.common.MoviePagedListAdapter
import com.profitmobile.valiev.cinemate.utils.EventObserver
import com.profitmobile.valiev.cinemate.utils.GridMarginDecoration
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


/**
 *[Fragment]  помогает пользователям искать определенные фильмы.
 */
class SearchMoviesFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val searchViewModel: SearchMoviesViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var binding : FragmentSearchMoviesBinding
    private lateinit var movieAdapter : MoviePagedListAdapter

    override fun onAttach(context: Context) {
        // Dagger Fragments, inject
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchMoviesBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.searchViewModel = searchViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        setToolbarTitle()
        initSearchInputListener()
        initMoviesAdapter()
        populateUi()
        navigateToDetails()
    }

    private fun initSearchInputListener() {
        binding.input.setOnEditorActionListener { view: View, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch(view)
                true
            } else {
                false
            }
        }
        binding.input.setOnKeyListener { view: View, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                doSearch(view)
                true
            } else {
                false
            }
        }
    }

    private fun doSearch(v: View) {
        val query = binding.input.text.toString()
        dismissKeyboard(v.windowToken)
        searchViewModel.setQuery(query)
    }

    private fun dismissKeyboard(windowToken: IBinder) {
        val imm = activity?.getSystemService(
            Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun initMoviesAdapter() {
        movieAdapter = MoviePagedListAdapter(
            movieClickCallback = { searchViewModel.navigateToDetailsEvent(it) },
            retryCallback = { searchViewModel.retry() }
        )

        binding.rvSearchMovieList.adapter = movieAdapter
        binding.rvSearchMovieList.addItemDecoration(
            GridMarginDecoration(requireContext(),R.dimen.grid_item_spacing)
        )


        val manager = GridLayoutManager(activity, resources.getInteger(R.integer.span_count))
        binding.rvSearchMovieList.layoutManager = manager
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) =
                when (movieAdapter.getItemViewType(position)) {
                    R.layout.network_state -> manager.spanCount
                    else -> 1
                }
        }
    }

    private fun populateUi() {
        // Observe search result data.
        searchViewModel.pagedData.observe(viewLifecycleOwner, Observer {
            movieAdapter.submitList(it)
        })

        // Observe состояние сети search result.
        searchViewModel.networkState.observe(viewLifecycleOwner, Observer {
            movieAdapter.setNetworkState(it)
        })
    }

    private fun setToolbarTitle(){
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.search)
    }

    private fun navigateToDetails(){
        searchViewModel.navigateToMovieDetails.observe(viewLifecycleOwner, EventObserver {
            val intent = Intent(requireContext(), DetailsActivity::class.java)
            intent.putExtra(EXTRA_MOVIE_ID, it)
            startActivity(intent)
        })
    }
}
