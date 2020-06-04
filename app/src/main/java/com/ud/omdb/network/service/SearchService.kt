package com.ud.omdb.network.service

import com.ud.omdb.model.MovieDetails
import com.ud.omdb.model.SearchResult
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    @GET("/")
    suspend fun loadMovieDetails(
        @Query("apiKey") apiKey: String,
        @Query("i") id: String
    ): MovieDetails


    @GET("/")
    suspend fun search(
        @Query("apiKey") apiKey: String,
        @Query("s") search: String,
        @Query("page") page: Int
    ): SearchResult
}