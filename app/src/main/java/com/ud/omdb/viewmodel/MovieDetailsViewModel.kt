package com.ud.omdb.viewmodel

import android.app.Application
import android.widget.ImageView
import androidx.lifecycle.AndroidViewModel
import com.squareup.picasso.Picasso
import com.ud.omdb.BuildConfig
import com.ud.omdb.model.MovieDetails
import com.ud.omdb.network.NetworkClient
import com.ud.omdb.network.service.SearchService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieDetailsViewModel(private val app: Application) : AndroidViewModel(app) {

    private var searchService: SearchService =
        NetworkClient(app).createService(SearchService::class.java)

    suspend fun loadMovieDetails(id: String): MovieDetails {
        return withContext(Dispatchers.IO) {
            searchService.loadMovieDetails(BuildConfig.API_KEY, id)
        }
    }

    fun loadPoster(url: String, poster: ImageView) {
        Picasso.with(app)
            .load(url)
            .into(poster)
    }
}