package com.ud.omdb.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.ud.omdb.BuildConfig
import com.ud.omdb.model.MovieDetails
import com.ud.omdb.model.SearchResult
import com.ud.omdb.network.NetworkClient
import com.ud.omdb.network.service.SearchService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainViewModel(private val activity: Activity) : ViewModel() {

    private var searchService: SearchService =
        NetworkClient(activity).createService(SearchService::class.java)

    //pagination
    private val pageSize: Int = 10
    var currentPage: Int = 1
    var maxPages: Int = currentPage
    var isLoading = false


    suspend fun loadMoviesList(searchedTitle: String): SearchResult {
        return withContext(Dispatchers.IO) {
            searchService.search(
                BuildConfig.API_KEY, searchedTitle, currentPage
            )
        }
    }

    suspend fun loadMovieDetails(id: String): MovieDetails {
        return withContext(Dispatchers.IO) {
            searchService.loadMovieDetails(BuildConfig.API_KEY, id)
        }
    }

    fun resetPagination() {
        currentPage = 1
        maxPages = currentPage
    }

    fun calculateMaxPages(resultsCount: Int) {
        val modulo = resultsCount % pageSize
        maxPages = if (modulo > 0) {
            (resultsCount / pageSize) + 1
        } else {
            resultsCount / pageSize
        }
    }

}