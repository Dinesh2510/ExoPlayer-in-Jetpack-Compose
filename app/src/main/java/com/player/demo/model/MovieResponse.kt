package com.player.demo.model

import com.google.gson.annotations.SerializedName

data class MovieResponse (

    @SerializedName("result") var result : ArrayList<MovieModel>

)