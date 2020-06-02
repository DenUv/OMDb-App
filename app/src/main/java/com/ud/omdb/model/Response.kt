package com.ud.omdb.model

import com.google.gson.annotations.SerializedName

class Response(
    @SerializedName("Title")
    val title: String,
    @SerializedName("Year")
    val year: Int,
    @SerializedName("Release")
    val released: String,
    @SerializedName("Genre")
    val genre: String,
    @SerializedName("Plot")
    val plot: String,
    @SerializedName("Poster")
    val poster: String,
    @SerializedName("Response")
    val success: Boolean,
    @SerializedName("Error")
    val errorMessage: String
)

