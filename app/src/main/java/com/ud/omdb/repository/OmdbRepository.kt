package com.ud.omdb.repository

import com.ud.omdb.BuildConfig
import com.ud.omdb.model.MovieDetails
import com.ud.omdb.model.SearchResult
import com.ud.omdb.network.NetworkClient
import com.ud.omdb.network.service.SearchService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object OmdbRepository {

    private var searchService: SearchService =
        NetworkClient().createService(SearchService::class.java)

    suspend fun loadMovieDetails(id: String): MovieDetails {
        return withContext(Dispatchers.IO) {
            searchService.loadMovieDetails(
                BuildConfig.API_KEY, id)
        }
    }

    suspend fun loadMoviesList(searchedTitle: String, currentPage: Int): SearchResult {
        return withContext(Dispatchers.IO) {
            searchService.search(
                BuildConfig.API_KEY, searchedTitle, currentPage
            )
        }
    }

}