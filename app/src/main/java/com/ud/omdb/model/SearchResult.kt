package com.ud.omdb.model

import com.google.gson.annotations.SerializedName

data class SearchResult(
    @SerializedName("Search")
    val list: List<MovieDetails>,
    @SerializedName("totalResults")
    val totalResults: Int,
    @SerializedName("Response")
    val success: Boolean,
    @SerializedName("Error")
    val errorMessage: String

)