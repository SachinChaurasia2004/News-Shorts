package com.example.newsshorts.presentation.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.newsshorts.R

@Composable
fun ImageHolder(
    modifier: Modifier = Modifier,
    imageUrl: String?
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
       modifier =  modifier.clip(RoundedCornerShape(4.dp))
            .fillMaxWidth()
           .aspectRatio(16 / 9f),
        placeholder = painterResource(R.drawable.placeholder),
        error = painterResource(R.drawable.placeholder)
    )
}