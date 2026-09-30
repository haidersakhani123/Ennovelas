package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BorderLight
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.YellowPrimary

@Composable
fun SideMenuDrawer(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .testTag("side_menu_drawer"),
        color = PureWhite,
        shadowElevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            // HEADER OF DRAWER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(GreenPrimary.copy(alpha = 0.15f), PureWhite)
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AppPlayIcon(size = 32.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "En",
                                color = TextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "novelas",
                                color = BluePrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Series, novelas y películas gratis",
                            color = TextMuted,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("drawer_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar menú",
                            tint = TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = BorderLight, thickness = 1.dp)

            Spacer(modifier = Modifier.height(10.dp))

            // Navigation Items
            DrawerNavGroup(title = "EXPLORAR") {
                DrawerItem(
                    label = "Inicio",
                    icon = Icons.Default.Home,
                    isSelected = currentRoute == "home",
                    onClick = { onNavigate("home"); onClose() }
                )
                DrawerItem(
                    label = "Tendencias",
                    icon = Icons.Default.Whatshot,
                    isSelected = currentRoute == "trending",
                    onClick = { onNavigate("trending"); onClose() }
                )
                DrawerItem(
                    label = "Últimos Episodios",
                    icon = Icons.Default.VideoLibrary,
                    isSelected = currentRoute == "latest",
                    onClick = { onNavigate("latest"); onClose() }
                )
                DrawerItem(
                    label = "Categorías / Novelas",
                    icon = Icons.Default.Category,
                    isSelected = currentRoute == "categories",
                    onClick = { onNavigate("categories"); onClose() }
                )
                DrawerItem(
                    label = "Películas",
                    icon = Icons.Default.Movie,
                    isSelected = currentRoute == "movies",
                    onClick = { onNavigate("movies"); onClose() }
                )
                DrawerItem(
                    label = "Buscar",
                    icon = Icons.Default.Search,
                    isSelected = currentRoute == "search",
                    onClick = { onNavigate("search"); onClose() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = BorderLight, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))

            DrawerNavGroup(title = "MI BIBLIOTECA") {
                DrawerItem(
                    label = "Favoritos",
                    icon = Icons.Default.Favorite,
                    isSelected = currentRoute == "favorites",
                    onClick = { onNavigate("favorites"); onClose() }
                )
                DrawerItem(
                    label = "Lista de Seguimiento",
                    icon = Icons.Default.Bookmark,
                    isSelected = currentRoute == "watchlist",
                    onClick = { onNavigate("watchlist"); onClose() }
                )
                DrawerItem(
                    label = "Notificaciones",
                    icon = Icons.Default.Notifications,
                    isSelected = currentRoute == "notifications",
                    onClick = { onNavigate("notifications"); onClose() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = BorderLight, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))

            DrawerNavGroup(title = "APLICACIÓN") {
                DrawerItem(
                    label = "Ajustes",
                    icon = Icons.Default.Settings,
                    isSelected = currentRoute == "settings",
                    onClick = { onNavigate("settings"); onClose() }
                )
                DrawerItem(
                    label = "Apoyar la App (Ver anuncio)",
                    icon = Icons.Default.Whatshot,
                    isSelected = false,
                    onClick = { onNavigate("support_ad"); onClose() }
                )
                DrawerItem(
                    label = "Política de Privacidad",
                    icon = Icons.Default.Lock,
                    isSelected = false,
                    onClick = { onNavigate("privacy_policy"); onClose() }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Ennovelas • enpantallatv.me",
                color = TextMuted,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 24.dp, bottom = 20.dp)
            )
        }
    }
}

@Composable
private fun DrawerNavGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = TextMuted,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
        )
        content()
    }
}

@Composable
private fun DrawerItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) GreenPrimary.copy(alpha = 0.12f) else Color.Transparent
    val contentColor = if (isSelected) GreenDark else TextPrimary
    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) GreenPrimary else TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = label,
            color = contentColor,
            fontSize = 13.5.sp,
            fontWeight = fontWeight
        )
    }
}
