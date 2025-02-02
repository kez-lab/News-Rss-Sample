package com.sample.news

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sample.news.theme.AppTheme
import kotlinx.coroutines.withTimeoutOrNull

@Composable
internal fun App() = AppTheme {
    NewsCrawlingScreen()
}

@Composable
fun NewsCrawlingScreen() {
    var newsData by remember { mutableStateOf<List<NewsItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        withTimeoutOrNull(5000) {
            try {
                newsData = fetchRssFeed("https://corsproxy.io/?url=https://www.yonhapnewstv.co.kr/browse/feed")
            } catch (e: Exception) {
                println(e)
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> CircularProgressIndicator()
            errorMessage != null -> Text(text = errorMessage ?: "오류 발생")
            newsData.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(newsData) { newsItem ->
                        NewsCard(newsItem)
                    }
                }
            }

            else -> Text(text = "뉴스 데이터를 찾을 수 없습니다.")
        }
    }
}

@Composable
fun NewsCard(newsItem: NewsItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = newsItem.title.toString(), style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = newsItem.description.toString(), style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "기사 전문:", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = newsItem.contentEncoded.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "출처: ${newsItem.link}", style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = newsItem.pubDate.toString(),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
