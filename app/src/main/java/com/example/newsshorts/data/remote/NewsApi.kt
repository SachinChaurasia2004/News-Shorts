package com.example.newsshorts.data.remote

import com.example.newsshorts.util.Constants
import com.example.newsshorts.domain.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("top-headlines")
    suspend fun getNews(
        @Query("category") category: String,
        @Query("country") country: String = "us",
        @Query("apiKey") apikey: String = Constants.API_KEY,
        //@Query("page") page: Int
    ): NewsResponse

    @GET("everything")
    suspend fun searchForNews(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String = Constants.API_KEY
    ): NewsResponse
}
