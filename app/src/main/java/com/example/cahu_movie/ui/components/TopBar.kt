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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions

@Composable
fun TopBar(
    isSearchActive: Boolean = false,
    searchQuery: String = "",
    onMenuClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onSearchSubmit: () -> Unit = {},
    onCloseSearch: () -> Unit = {}
) {
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
        if (isSearchActive) {
            IconButton(onClick = onCloseSearch) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "Tìm kiếm phim",
                        color = Color(0xFF9CA3AF)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onCloseSearch) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Dong tim kiem",
                            tint = Color.White
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF1B1B27),
                    unfocusedContainerColor = Color(0xFF1B1B27),
                    focusedIndicatorColor = Color(0xFFE50914),
                    unfocusedIndicatorColor = Color(0xFF343445),
                    cursorColor = Color(0xFFE50914)
                ),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearchSubmit()
                    }
                )
            )
            return@Row
        }

        IconButton(onClick = onMenuClick) {
            Icon(
                imageVector = Icons.Outlined.Menu,
                contentDescription = "Mo sidebar",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            text = "Cahu Movie",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onSearchClick) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Tim kiem",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }

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
