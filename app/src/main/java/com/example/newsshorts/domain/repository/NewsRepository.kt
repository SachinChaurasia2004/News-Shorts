package com.example.newsshorts.domain.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.newsshorts.data.remote.NewsApi
import com.example.newsshorts.domain.model.Article
import com.example.newsshorts.domain.model.NewsResponse
import com.example.newsshorts.util.Resource
import retrofit2.Response
import javax.inject.Inject

interface NewsRepository {
    suspend fun getNews(
        category: String
    ): Resource<List<Article>>

    suspend fun searchNews(
        query: String
    ): Resource<List<Article>>
}



//class NewsRepository @Inject constructor(
//    private val newsApiService: NewsApiService
//) {
//    fun getNews() = Pager(
//        config = PagingConfig(
//            pageSize = 20,
//        ),
//        pagingSourceFactory = {
//            NewsPagingSource(newsApiService)
//        }
//    ).flow
//}


