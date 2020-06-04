package com.ud.omdb.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding2.widget.RxTextView
import com.ud.omdb.R
import com.ud.omdb.activity.MainActivity
import com.ud.omdb.databinding.FragmentSearchBinding
import com.ud.omdb.listener.OnItemTouchListener
import com.ud.omdb.model.SearchResult
import com.ud.omdb.recycler.MovieListAdapter
import com.ud.omdb.recycler.PaginationListener
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private lateinit var titleInput: TextInputLayout
    private lateinit var titleEditText: EditText
    private lateinit var message: TextView
    private lateinit var movieListRecycler: RecyclerView

    private lateinit var movieListAdapter: MovieListAdapter

    private lateinit var parentActivity: MainActivity

    private lateinit var searchedTitle: String
    private lateinit var searchResult: SearchResult

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentActivity = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initDataBinding(layoutInflater, container)
        initRecyclerView()
        subscribeOnSearchInput()
        return binding.root
    }

    private fun initDataBinding(layoutInflater: LayoutInflater, container: ViewGroup?) {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_search, container, false)

        titleInput = binding.tilSearchField
        titleEditText = binding.etSearchField
        message = binding.tvMessage
        movieListRecycler = binding.rvMovieList
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(parentActivity)

        movieListRecycler.layoutManager = layoutManager
        movieListAdapter = MovieListAdapter(parentActivity, parentActivity)
        movieListRecycler.adapter = movieListAdapter

        movieListRecycler.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadNextPage() {
                if (parentActivity.currentPage < parentActivity.maxPages) {
                    loadMoreMovies()
                }
            }

            override fun isLoading(): Boolean {
                return parentActivity.isLoading
            }

        })
    }

    @SuppressLint("CheckResult")
    private fun subscribeOnSearchInput() {
        val searchInputObservable = RxTextView.textChanges(titleEditText)
            .map { titleEditText.text.toString() }
            .filter { it.length >= 3 }
            .debounce(800, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()

        searchInputObservable.subscribe(
            {
                searchedTitle = it
                CoroutineScope(Dispatchers.IO).launch {
                    searchForMovie()
                }
            },
            { message.text = it.localizedMessage }
        )
    }

    //Network
    private fun searchForMovie() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                searchResult = parentActivity.loadMoviesList(searchedTitle)
            } catch (exp: Exception) {
                message.text = exp.localizedMessage
                cancel()
            }
            resetPagination()
            calculateMaxPages(searchResult.totalResults)
            handleResponse()
        }
    }

    private fun loadMoreMovies() {
        CoroutineScope(Dispatchers.Main).launch {
            //TODO try to move out of withContext
            withContext(coroutineContext) {
                parentActivity.isLoading = true
                ++parentActivity.currentPage
                movieListAdapter.showLoader()
            }
            try {
                searchResult = parentActivity.loadMoviesList(searchedTitle)
            } catch (exp: Exception) {
                message.text = exp.localizedMessage
                --parentActivity.currentPage
                cancel()
            }
            withContext(coroutineContext) {
                movieListAdapter.hideLoader()
                parentActivity.isLoading = false
                handleResponse()
            }
        }
    }

    private suspend fun handleResponse() {
        withContext(Dispatchers.Main) {
            if (searchResult.success) {
                movieListAdapter.addItems(searchResult.list)
            } else {
                //TODO -> rework to show error and not hide list
                movieListRecycler.visibility = View.GONE
                message.text = searchResult.errorMessage
            }
        }
    }

    //Pagination
    private fun resetPagination() {
        movieListAdapter.clear()
        movieListRecycler.visibility = View.VISIBLE
        message.text = null
        parentActivity.currentPage = 1
        parentActivity.maxPages = parentActivity.currentPage
    }

    private fun calculateMaxPages(resultsCount: Int) {
        val modulo = resultsCount % parentActivity.pageSize
        parentActivity.maxPages = if (modulo > 0) {
            (resultsCount / parentActivity.pageSize) + 1
        } else {
            resultsCount / parentActivity.pageSize
        }
    }


}
