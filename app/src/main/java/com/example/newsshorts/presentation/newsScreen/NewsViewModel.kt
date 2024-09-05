package com.example.newsshorts.presentation.newsScreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsshorts.domain.model.Article
import com.example.newsshorts.domain.repository.NewsRepository
import com.example.newsshorts.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NewsScreenState())
    val state: StateFlow<NewsScreenState> = _state

    private var searchJob: kotlinx.coroutines.Job? = null

    fun onEvent(event: NewsScreenEvent) {
        when (event) {
            is NewsScreenEvent.OnCategoryChanged -> {
                _state.value = _state.value.copy(category = event.category)
                getNewsArticles(event.category)
            }
            NewsScreenEvent.OnCloseIconClicked -> {
                _state.value = _state.value.copy(isSearchBarVisible = false)
                getNewsArticles(_state.value.category)
            }

            is NewsScreenEvent.OnNewsCardClicked -> {
                _state.value = _state.value.copy(selectedArticle = event.article)
            }
            NewsScreenEvent.OnSearchIconClicked -> {
                _state.value = _state.value.copy(isSearchBarVisible = true)
            }
            is NewsScreenEvent.OnSearchQueryChanged -> {
                _state.value = _state.value.copy(searchQuery = event.searchQuery)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(1000)
                    searchNews(_state.value.searchQuery)
                }
            }
        }
    }

    private fun getNewsArticles(category: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = newsRepository.getNews(category = category)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        articles = result.data ?: emptyList(),
                        isLoading = false,
                        error = null
                    )
                    Log.d("NewsViewModel", "Success fetching news: ${result.data}")
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = result.message,
                        isLoading = false
                    )
                    Log.e("NewsViewModel", "Error fetching news: ${result.message}")
                }
            }
        }
    }

    private fun searchNews(query: String) {
        if (query.isEmpty()){
//            Log.d("NewsViewModel", "Empty query")
//            getNewsArticles(_state.value.category)
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = newsRepository.searchNews(query)) {
                is Resource.Success -> {
                    Log.d("NewsViewModel", "Success querying news: ${result.data}")
                    _state.value = _state.value.copy(
                        articles = result.data ?: emptyList(),
                        isLoading = false,
                        error = null
                    )
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = result.message,
                        isLoading = false
                    )
                    Log.e("NewsViewModel", "Error fetching news: ${result.message}")
                }
            }

        }
    }
}



//@HiltViewModel
//class NewsViewModel @Inject constructor(
//    private val repository: NewsRepository,
//): ViewModel() {
//
//    fun getBreakingNews(): Flow<PagingData<Article>> = repository.getNews().cachedIn(viewModelScope)
//}


