package com.example.cahu_movie.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SidebarItem(
    val title: String,
    val icon: ImageVector
)

private val sidebarItems = listOf(
    SidebarItem("Trang chu", Icons.Outlined.Home),
    SidebarItem("Phim moi", Icons.Outlined.Movie),
    SidebarItem("Yeu thich", Icons.Outlined.FavoriteBorder),
    SidebarItem("Da xem", Icons.Outlined.History),
    SidebarItem("Cai dat", Icons.Outlined.Settings),
    SidebarItem("Thong tin", Icons.Outlined.Info)
)

@Composable
fun AppSidebar(
    selectedItem: String,
    onItemClick: (SidebarItem) -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.78f),
        drawerContainerColor = Color(0xFF11111A),
        drawerContentColor = Color.White,
        drawerTonalElevation = DrawerDefaults.ModalDrawerElevation
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFF11111A))
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Cahu Movie",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Kho phim cua ban",
                color = Color(0xFF9CA3AF),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFF272736))
            Spacer(modifier = Modifier.height(12.dp))

            sidebarItems.forEach { item ->
                val selected = item.title == selectedItem

                NavigationDrawerItem(
                    label = {
                        Text(
                            text = item.title,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    selected = selected,
                    onClick = { onItemClick(item) },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFFE50914),
                        unselectedContainerColor = Color.Transparent,
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color(0xFFB8B8C7),
                        selectedTextColor = Color.White,
                        unselectedTextColor = Color(0xFFB8B8C7)
                    ),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
