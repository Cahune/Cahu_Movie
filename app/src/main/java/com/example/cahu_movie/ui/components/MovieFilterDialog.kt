package com.example.cahu_movie.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cahu_movie.ui.home.MovieFilterOption
import com.example.cahu_movie.ui.home.movieFilterGroups

@Composable
fun MovieFilterDialog(
    selectedFilters: List<MovieFilterOption>,
    onApplyFilters: (List<MovieFilterOption>) -> Unit,
    onDismiss: () -> Unit
) {
    var pendingFilters by remember(selectedFilters) {
        mutableStateOf(selectedFilters)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF17131F),
        titleContentColor = Color.White,
        textContentColor = Color.White,
        title = {
            Text(
                text = "Lọc phim",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn {
                movieFilterGroups.forEach { (groupTitle, options) ->
                    item {
                        Text(
                            text = groupTitle,
                            color = Color(0xFFB8B1C2),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(
                                top = 10.dp,
                                bottom = 6.dp
                            )
                        )
                    }

                    items(options) { option ->
                        val selected = option in pendingFilters

                        FilterChip(
                            selected = selected,
                            onClick = {
                                pendingFilters =
                                    if (selected) {
                                        pendingFilters - option
                                    } else {
                                        pendingFilters + option
                                    }
                            },
                            label = {
                                Text(option.title)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFE50914),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF221D2C),
                                labelColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApplyFilters(pendingFilters)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE50914),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Lọc",
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (pendingFilters.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            pendingFilters = emptyList()
                            onApplyFilters(emptyList())
                        }
                    ) {
                        Text(
                            text = "Bỏ lọc",
                            color = Color.White
                        )
                    }
                }

                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Đóng",
                        color = Color.White
                    )
                }
            }
        }
    )
}
