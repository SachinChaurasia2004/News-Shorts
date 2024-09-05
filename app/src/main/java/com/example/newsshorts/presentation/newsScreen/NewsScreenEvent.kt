package com.example.newsshorts.presentation.newsScreen

import com.example.newsshorts.domain.model.Article

sealed class NewsScreenEvent {
    data class OnNewsCardClicked(val article: Article) : NewsScreenEvent()
    data class OnCategoryChanged(val category: String) : NewsScreenEvent()
    data class OnSearchQueryChanged(val searchQuery: String) : NewsScreenEvent()
    data object OnSearchIconClicked : NewsScreenEvent()
    data object OnCloseIconClicked : NewsScreenEvent()
}