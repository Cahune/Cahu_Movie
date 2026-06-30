package com.example.cahu_movie.ui.components

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.FilterChip

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(Color(0xFF11111A))
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.Outlined.Menu,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = "Cahu Movie",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(26.dp)
        )

    }
}
//@Composable
//fun CategoryFilter() {
//
//    val categories = listOf(
//        "All",
//        "Movie",
//        "TV",
//        "Anime",
//        "Trending",
//        "Top IMDB"
//    )
//
//    LazyRow(
//        horizontalArrangement = Arrangement.spacedBy(10.dp),
//        contentPadding = PaddingValues(horizontal = 16.dp)
//    ) {
//
//        items(categories) { item ->
//
//            FilterChip(
//                selected = item == "All",
//                onClick = { },
//                label = {
//                    Text(item)
//                }
//            )
//
//        }
//
//    }
//
//}