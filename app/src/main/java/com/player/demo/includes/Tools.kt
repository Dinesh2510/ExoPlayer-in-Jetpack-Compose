package com.player.demo.includes


import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.player.demo.model.MovieModel
import com.player.demo.model.MovieResponse
import java.io.IOException
import java.io.InputStream


object Tools {
    private val gsonConvertor by lazy { Gson() }



    fun parseJsonFromAssets(context: Context,fileName: String): List<MovieModel>{

        // Read the JSON file from assets
        val jsonString = try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }

        // Use Gson to convert the JSON string into a list of movies
        return try {
            val moviesResponse = Gson().fromJson(jsonString, MovieResponse::class.java)
            moviesResponse.result
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}