package com.ud.omdb.network.service

import com.ud.omdb.model.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    @GET("/")
    suspend fun findMovieByTitle(
        @Query("apiKey") apiKey: String,
        @Query("t") title: String
    ): Response

}