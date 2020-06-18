package com.ud.omdb.fragment

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding2.widget.RxTextView
import com.ud.omdb.R
import com.ud.omdb.databinding.FragmentSearchBinding
import com.ud.omdb.listener.OnItemTouchListener
import com.ud.omdb.recycler.MovieListAdapter
import com.ud.omdb.recycler.PaginationListener
import com.ud.omdb.viewmodel.SearchViewModel
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private lateinit var titleInput: TextInputLayout
    private lateinit var titleEditText: EditText
    private lateinit var errorMessage: TextView
    private lateinit var movieListRecycler: RecyclerView

    private lateinit var movieListAdapter: MovieListAdapter

    private lateinit var onItemTouchListener: OnItemTouchListener

    private lateinit var viewModel: SearchViewModel

    private lateinit var searchInputDisposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        onItemTouchListener = requireActivity() as OnItemTouchListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initDataBinding(layoutInflater, container)
        initRecyclerView()
        searchInputDisposable = subscribeOnSearchInput()
        return binding.root
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
                false
            )
        }
        super.onHiddenChanged(hidden)
    }

    private fun initDataBinding(layoutInflater: LayoutInflater, container: ViewGroup?) {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_search, container, false)

        titleInput = binding.tilSearchField
        titleEditText = binding.etSearchField
        errorMessage = binding.tvErrorMessage
        movieListRecycler = binding.rvMovieList
    }

    private fun initViewModel() {
        viewModel = SearchViewModel()
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(requireActivity())

        movieListRecycler.layoutManager = layoutManager
        movieListAdapter = MovieListAdapter(viewModel, onItemTouchListener)
        movieListRecycler.adapter = movieListAdapter

        movieListRecycler.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadNextPage() {
                if (viewModel.currentPage < viewModel.maxPages) {
                    loadMoreMovies()
                }
            }

            override fun isLoading(): Boolean {
                return viewModel.isLoading
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        searchInputDisposable.dispose()
    }

    private fun subscribeOnSearchInput(): Disposable {
        val searchInputObservable = RxTextView.textChanges(titleEditText)
            .map { titleEditText.text.toString() }
            .filter { it.length >= 3 || it.isEmpty() }
            .debounce(800, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()

        return searchInputObservable.subscribe(
            {
                if (it.isNotEmpty()) {
                    viewModel.searchedTitle = it
                    CoroutineScope(Dispatchers.IO).launch {
                        searchForMovie()
                    }
                }
            },
            { errorMessage.text = it.localizedMessage }
        )
    }

    //Network
    private fun searchForMovie() {
        viewModel.viewModelScope.launch {
            hideErrorMessage()
            hideOnLoadError()
            resetView()
            try {
                viewModel.loadMoviesList()
            } catch (exp: Exception) {
                showErrorMessage(exp.localizedMessage)
                return@launch
            }
            viewModel.calculateMaxPages()
            handleResponse()
        }
    }

    private fun loadMoreMovies() {
        viewModel.viewModelScope.launch {
            withContext(coroutineContext) {
                hideErrorMessage()
                hideOnLoadError()

                viewModel.isLoading = true
                ++viewModel.currentPage
                movieListAdapter.showLoader()
            }
            try {
                viewModel.loadMoviesList()
            } catch (exp: Exception) {
                showOnLoadError()
                movieListAdapter.hideLoader()
                --viewModel.currentPage
                return@launch
            }
            withContext(coroutineContext) {
                movieListAdapter.hideLoader()
                viewModel.isLoading = false
                handleResponse()
            }
        }
    }

    private suspend fun handleResponse() {
        withContext(coroutineContext) {
            if (viewModel.searchResult.success) {
                movieListAdapter.addItems(viewModel.searchResult.list)
            } else {
                showErrorMessage(viewModel.searchResult.errorMessage)
            }
        }
    }

    //Return view to initial state
    private fun resetView() {
        movieListAdapter.clear()
        movieListRecycler.visibility = View.VISIBLE
        errorMessage.text = null
        viewModel.resetPagination()
    }

    //Errors
    private fun showErrorMessage(message: String?) {
        errorMessage.visibility = View.VISIBLE
        errorMessage.text = message
    }

    private fun hideErrorMessage() {
        errorMessage.visibility = View.GONE
        errorMessage.text = null
    }

    private fun showOnLoadError() {
        errorMessage.visibility = View.VISIBLE
        errorMessage.setOnClickListener { loadMoreMovies() }
        errorMessage.text = getString(R.string.on_load_error)
    }

    private fun hideOnLoadError() {
        errorMessage.visibility = View.GONE
        errorMessage.setOnClickListener(null)
        errorMessage.text = null
    }

}
