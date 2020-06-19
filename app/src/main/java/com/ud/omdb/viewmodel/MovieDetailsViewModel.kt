package com.ud.omdb.viewmodel

import androidx.lifecycle.ViewModel
import com.ud.omdb.repository.OmdbRepository
import com.ud.omdb.model.MovieDetails

class MovieDetailsViewModel : ViewModel() {

    lateinit var movieDetails: MovieDetails

    suspend fun loadMovieDetails(id: String) {
        movieDetails = OmdbRepository.loadMovieDetails(id)
    }
}