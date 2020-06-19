package com.ud.omdb.viewmodel

import androidx.lifecycle.ViewModel
import com.ud.omdb.model.SearchResult
import com.ud.omdb.repository.OmdbRepository

class SearchViewModel : ViewModel() {

    //pagination
    private val pageSize: Int = 10
    var currentPage: Int = 1
    var maxPages: Int = currentPage
    var isLoading = false

    lateinit var searchedTitle: String

    lateinit var searchResult: SearchResult
        private set

    suspend fun loadMoviesList() {
        searchResult = OmdbRepository.loadMoviesList(searchedTitle, currentPage)
    }

    fun resetPagination() {
        currentPage = 1
        maxPages = currentPage
    }

    fun calculateMaxPages() {
        val searchResultCount = searchResult.totalResults

        val modulo = searchResultCount % pageSize
        maxPages = if (modulo > 0) {
            (searchResultCount / pageSize) + 1
        } else {
            searchResultCount / pageSize
        }
    }

    fun nextPage(){
        isLoading = true
        ++currentPage
    }

}