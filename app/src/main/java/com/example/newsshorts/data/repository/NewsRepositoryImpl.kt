package com.example.newsshorts.data.repository

import android.util.Log
import com.example.newsshorts.data.remote.NewsApi
import com.example.newsshorts.domain.model.Article
import com.example.newsshorts.domain.model.NewsResponse
import com.example.newsshorts.domain.repository.NewsRepository
import com.example.newsshorts.util.Resource
import retrofit2.Response
import javax.inject.Inject


class NewsRepositoryImpl @Inject constructor(
    private val newsApi: NewsApi
) : NewsRepository {

    override suspend fun getNews(category: String): Resource<List<Article>> {
        return try {
            val response = newsApi.getNews(category = category)
            Resource.Success(response.articles)
        } catch (e: Exception) {
            Resource.Error(message = "Failed to fetch news ${e.message}")
        }
    }

    override suspend fun searchNews(query: String): Resource<List<Article>> {
       return try {
           val response = newsApi.searchForNews(query = query)
           Resource.Success(response.articles)
       } catch (e: Exception) {
           Resource.Error(message = "Failed to search news ${e.message}")
       }
    }
}