package com.example.newsshorts.presentation.newsScreen

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.newsshorts.domain.model.Article
import com.example.newsshorts.presentation.components.BottomSheetContent
import com.example.newsshorts.presentation.components.CategoryTab
import com.example.newsshorts.presentation.components.NewsCard
import com.example.newsshorts.presentation.components.RetryContent
import com.example.newsshorts.presentation.components.SearchAppBar
import com.example.newsshorts.presentation.components.TopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NewsScreen(
    viewModel: NewsViewModel = hiltViewModel(),
    onEvent: (NewsScreenEvent) -> Unit,
    onReadFullStoryButtonClicked: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { 7 }
    )
    val categories = listOf(
        "General",
        "Business",
        "Entertainment",
        "Health",
        "Science",
        "Sports",
        "Technology"
    )

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var visibility by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    if (visibility) {
        ModalBottomSheet(
            onDismissRequest = {visibility = false },
            sheetState = sheetState,
            content = {
                state.selectedArticle?.let {
                    BottomSheetContent(
                        article = state.selectedArticle!!,
                        onReadFullStoryButtonClicked = {
                            onReadFullStoryButtonClicked(it.url)
                            coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    visibility = false
                                }
                            }
                        }
                    )
                }
            }
        )
    }


    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onEvent(NewsScreenEvent.OnCategoryChanged(categories[page]))
        }
    }
    LaunchedEffect(key1 = Unit) {
        if (state.searchQuery.isNotEmpty()){
            onEvent(NewsScreenEvent.OnSearchQueryChanged(searchQuery = state.searchQuery))
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Crossfade(targetState = state.isSearchBarVisible, label = "") { isVisible ->
            if (isVisible) {
               Scaffold { paddingValues ->
                   Column(
                       modifier = Modifier.fillMaxSize().padding(paddingValues)
                   ) {
                       SearchAppBar(
                           modifier = Modifier.focusRequester(focusRequester),
                           value = state.searchQuery,
                           onInputValueChange = {
                               onEvent(NewsScreenEvent.OnSearchQueryChanged(it))
                           },
                           onCloseIconClicked = { onEvent(NewsScreenEvent.OnCloseIconClicked) },
                           onSearchIconClicked = {
                               keyboardController?.hide()
                               focusManager.clearFocus()
                           }
                       )

                       NewsArticleList(
                           state = state,
                           onCardClicked = { article ->
                               visibility = true
                               onEvent(NewsScreenEvent.OnNewsCardClicked(article = article))
                           },
                           onRetry = {
                               onEvent(NewsScreenEvent.OnCategoryChanged(state.category))
                           }
                       )
                   }
               }
            } else {
                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        TopBar(
                            scrollBehavior = scrollBehavior,
                            onSearchClicked = {
                                coroutineScope.launch {
                                    delay(500)
                                    focusRequester.requestFocus()
                                }
                                onEvent(NewsScreenEvent.OnSearchIconClicked)
                            })
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        CategoryTab(
                            pagerState = pagerState,
                            categories = categories,
                            onTabSelected = { index ->
                                coroutineScope.launch { pagerState.animateScrollToPage(index) }
                            }
                        )

                        HorizontalPager(
                            state = pagerState
                        ) {
                            NewsArticleList(
                                state = state,
                                onCardClicked = { article ->
                                    visibility = true
                                    onEvent(NewsScreenEvent.OnNewsCardClicked(article = article))
                                },
                                onRetry = {
                                    onEvent(NewsScreenEvent.OnCategoryChanged(state.category))
                                }

                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsArticleList(
    state: NewsScreenState,
    onRetry: () -> Unit,
    onCardClicked: (Article) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            // Display loading indicator when loading
            state.isLoading -> {
                CircularProgressIndicator()
            }
            // Display error message and retry button if there is an error
            state.error != null -> {
                RetryContent(
                    error = state.error,
                    onRetry = onRetry
                )
            }
            // Display the list of articles if no loading or error
            else -> {
//                Log.d("NewsScreen", "Inside_success ${state.articles}")
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(state.articles.size) { index ->
                        NewsCard(
                            modifier = Modifier,
                            article = state.articles[index],
                            onCardClicked = onCardClicked
                        )
                    }
                }
            }
        }
    }
}




//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun NewsShortsScreen(viewModel: NewsViewModel = hiltViewModel()) {
//    val newsResult by viewModel.newsResult.observeAsState()
//    val pagerState = rememberPagerState(
//        initialPage = 0,
//        initialPageOffsetFraction = 0f
//    ){
//        10000
//    }
//
//    VerticalPager(
//        state = pagerState,
//        modifier = Modifier.fillMaxSize(),
//        pageSize = PageSize.Fill,
//        pageSpacing = 8.dp
//    ) { page ->
//        when (val result = newsResult) {
//            is NetworkResponse.Loading -> {
//                Box(
//                    modifier = Modifier.fillMaxSize()
//                ) {
//                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//                }
//            }
//            is NetworkResponse.Success -> {
//                if (result.data.results.isNotEmpty()) {
//                    Log.d("NewsScreen", "Inside_success ${result.data.status} = ${result.data.totalResults}")
//
//                    // Avoid index out of bounds
//                    if (page < result.data.results.size) {
//                        NewsShortItem(newsShort = result.data.results[page])
//                    }
//                } else {
//                    Log.e("Screen", "No results found!")
//                }
//            }
//            is NetworkResponse.Error -> {
//                Box(
//                    modifier = Modifier.fillMaxSize()
//                ) {
//                    Text(
//                        text = result.message,
//                        color = Color.Red,
//                        modifier = Modifier.align(Alignment.Center),
//                        style = MaterialTheme.typography.headlineSmall
//                    )
//                }
//            }
//            null -> {
//                Log.e("Screen", "newsResult is null!")
//            }
//        }
//    }
//}
//
//@Composable
//fun NewsShortItem(newsShort: Article) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//
//        AsyncImage(
//            modifier = Modifier
//                .height(300.dp)
//                .fillMaxWidth()
//                .clip(RoundedCornerShape(16.dp)),
//            model = newsShort.image_url,  // The URL of the image
//            contentDescription = null,
//            contentScale = ContentScale.Fit,
//            placeholder = painterResource(id = R.drawable.placeholder),
//            error = painterResource(id = R.drawable.placeholder)
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        newsShort.title?.let {
//            Text(
//                text = it,
//                fontWeight = FontWeight.Bold,
//                style = MaterialTheme.typography.headlineLarge
//            )
//        }
//        Spacer(modifier = Modifier.height(8.dp))
//
//        newsShort.description?.let {
//            Text(
//                text = it,
//                style = MaterialTheme.typography.labelLarge
//            )
//        }
//        Spacer(modifier = Modifier.height(8.dp))
//
//        newsShort.pubDate?.let {
//            Text(
//                text = it,
//                style = MaterialTheme.typography.bodyLarge
//            )
//        }
//    }
//}

//@Composable
//fun PagingListScreen() {
//    val viewModel = hiltViewModel<NewsViewModel>()
//
//    val articles = viewModel.getBreakingNews().collectAsLazyPagingItems()
//
//    Box(
//        modifier = Modifier.fillMaxSize()
//        .padding(start = 16.dp,end = 16.dp)
//    ) {
//        LazyColumn {
//            items(
//                 articles.itemCount
//            ) { article ->
//                Text(
//                    modifier = Modifier
//                        .height(75.dp),
//                    text = articles[article]!!.title,
//                )
//
//                HorizontalDivider()
//            }
//
//            when (val state = articles.loadState.refresh) { //FIRST LOAD
//                is LoadState.Error -> {
//                    //TODO Error Item
//                    //state.error to get error message
//                }
//                is LoadState.Loading -> { // Loading UI
//                    item {
//                        Column(
//                            modifier = Modifier
//                                .fillParentMaxSize(),
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.Center,
//                        ) {
//                            Text(
//                                modifier = Modifier
//                                    .padding(8.dp),
//                                text = "Refresh Loading"
//                            )
//
//                            CircularProgressIndicator(color = Color.Black)
//                        }
//                    }
//                }
//                else -> {}
//            }
//
//            when (articles.loadState.append) { // Pagination
//                is LoadState.Error -> {
//                    //TODO Pagination Error Item
//                    //state.error to get error message
//                }
//                is LoadState.Loading -> { // Pagination Loading UI
//                    item {
//                        Column(
//                            modifier = Modifier
//                                .fillMaxWidth(),
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.Center,
//                        ) {
//                            Text(text = "Pagination Loading")
//
//                            CircularProgressIndicator(color = Color.Black)
//                        }
//                    }
//                }
//                else -> {}
//            }
//        }
//    }
//}

