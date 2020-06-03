package com.ud.omdb.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.ud.omdb.BuildConfig
import com.ud.omdb.R
import com.ud.omdb.databinding.ActivityMainBinding
import com.ud.omdb.model.MovieDetails
import com.ud.omdb.model.SearchResult
import com.ud.omdb.network.NetworkClient
import com.ud.omdb.network.service.SearchService
import com.ud.omdb.recycler.MovieListAdapter
import com.ud.omdb.recycler.PaginationListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var searchService: SearchService

    private lateinit var binding: ActivityMainBinding

    private lateinit var titleInput: TextInputLayout
    private lateinit var titleEditText: EditText
    private lateinit var message: TextView
    private lateinit var movieListRecycler: RecyclerView

    private lateinit var searchButton: Button

    private lateinit var movieListAdapter: MovieListAdapter

    private lateinit var movieDetails: MovieDetails
    private lateinit var searchResult: SearchResult

    private var currentPage: Int = 1
    private var maxPages: Int = currentPage
    private val pageSize: Int = 10
    var isLoading = false
        private set

    private lateinit var searchedMovie: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initDataBinding()
        initRecyclerView()

        initServices()
    }

    private fun initServices() {
        searchService = NetworkClient(this).createService(SearchService::class.java)
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        titleInput = binding.tilSearchField
        titleEditText = binding.etSearchField
        searchButton = binding.btnSearch
        message = binding.tvMessage
        movieListRecycler = binding.rvMovieList

        binding.activity = this
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)

        movieListRecycler.layoutManager = layoutManager
        movieListAdapter = MovieListAdapter(this)
        movieListRecycler.adapter = movieListAdapter

        movieListRecycler.addOnScrollListener(
            object : PaginationListener(layoutManager) {
                override fun loadNextPage() {
                    if (currentPage < maxPages) {
                        loadMoreMovies()
                    }
                }

                override fun isLoading(): Boolean {
                    return isLoading
                }
            })
    }

    fun submit() {
        //TODO add validation
        searchedMovie = titleEditText.text.toString()
        searchForMovie()
    }

    suspend fun loadMovie() {
        withContext(Dispatchers.IO) {
            movieDetails = searchService.findMovieByTitle(
                BuildConfig.API_KEY,
                searchedMovie
            )
        }
    }

    private fun searchForMovie() {
        resetPagination()
        CoroutineScope(Dispatchers.Main).launch {
            searchResult = loadMovies()
            handleResponse(searchResult.success)
            calculateMaxPages(searchResult.totalResults)
        }
    }

    private fun loadMoreMovies() {
        CoroutineScope(Dispatchers.Main).launch {
            isLoading = true
            ++currentPage
            withContext(coroutineContext) {
                movieListAdapter.showLoader()
            }
            searchResult = loadMovies()
            withContext(coroutineContext) {
                movieListAdapter.hideLoader()
            }
            handleResponse(searchResult.success)
            isLoading = false
        }
    }

    private suspend fun loadMovies(): SearchResult {
        return withContext(Dispatchers.IO) {
            searchService.searchMovie(
                BuildConfig.API_KEY,
                searchedMovie,
                currentPage
            )
        }
    }

    private fun calculateMaxPages(resultsCount: Int) {
        val modulo = resultsCount % pageSize
        maxPages = if (modulo > 0) {
            (resultsCount / pageSize) + 1
        } else {
            resultsCount / pageSize
        }
    }

    private fun resetPagination() {
        movieListRecycler.visibility = View.VISIBLE
        message.text = null
        currentPage = 1
        maxPages = currentPage
        movieListAdapter.clear()
    }

    suspend fun handleResponse(success: Boolean) {
        withContext(Dispatchers.Main) {
            if (success) {
                movieListAdapter.addItems(searchResult.list)
            } else {
                movieListRecycler.visibility = View.GONE
                message.text = searchResult.errorMessage
            }
        }
    }
}

