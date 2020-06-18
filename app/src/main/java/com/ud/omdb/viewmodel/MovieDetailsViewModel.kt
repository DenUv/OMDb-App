package com.ud.omdb.viewmodel

import androidx.lifecycle.ViewModel
import com.ud.omdb.BuildConfig
import com.ud.omdb.model.MovieDetails
import com.ud.omdb.network.NetworkClient
import com.ud.omdb.network.service.SearchService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieDetailsViewModel: ViewModel() {

    private var searchService: SearchService =
        NetworkClient().createService(SearchService::class.java)

    suspend fun loadMovieDetails(id: String): MovieDetails {
        return withContext(Dispatchers.IO) {
            searchService.loadMovieDetails(BuildConfig.API_KEY, id)
        }
    }

}