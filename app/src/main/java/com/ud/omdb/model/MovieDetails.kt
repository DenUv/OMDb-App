package com.ud.omdb.model

import com.google.gson.annotations.SerializedName

class MovieDetails(
    @SerializedName("Title")
    val title: String,
    @SerializedName("Year")
    val year: String,
    @SerializedName("Released")
    val released: String,
    @SerializedName("Genre")
    val genre: String,
    @SerializedName("Plot")
    val plot: String,
    @SerializedName("Poster")
    val poster: String,
    @SerializedName("Response")
    val success: Boolean,

    @SerializedName("imdbID")
    val id: String,

    @SerializedName("Error")
    val errorMessage: String
)

