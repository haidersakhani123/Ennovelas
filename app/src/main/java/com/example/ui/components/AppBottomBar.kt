package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BrandRed
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted

/**
 * Modern, Sleek App Bottom Navigation Bar:
 * Items:
 * 1. Inicio (Home)
 * 2. Capítulos (Episodes)
 * 3. Películas (Movies)
 * 4. Favoritos (Favorites)
 * 5. Buscar (Search)
 */
@Composable
fun AppBottomBar(
    currentRoute: String,
    favoriteCount: Int,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 6.dp)
            .testTag("app_bottom_bar")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .height(48.dp)
                .align(Alignment.BottomCenter)
                .shadow(8.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(PureWhite)
                .border(0.6.dp, BorderLight.copy(alpha = 0.8f), RoundedCornerShape(24.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. INICIO
                BottomNavItem(
                    label = "Inicio",
                    selected = currentRoute == "home",
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    onClick = { onNavigate("home") },
                    modifier = Modifier.weight(1f)
                )

                // 2. CAPÍTULOS
                BottomNavItem(
                    label = "Capítulos",
                    selected = currentRoute == "all_episodes",
                    selectedIcon = Icons.Filled.Tv,
                    unselectedIcon = Icons.Outlined.Tv,
                    onClick = { onNavigate("all_episodes") },
                    modifier = Modifier.weight(1f)
                )

                // 3. PELÍCULAS (Requested by user)
                BottomNavItem(
                    label = "Películas",
                    selected = currentRoute == "all_movies",
                    selectedIcon = Icons.Filled.Movie,
                    unselectedIcon = Icons.Outlined.Movie,
                    onClick = { onNavigate("all_movies") },
                    modifier = Modifier.weight(1f)
                )

                // 4. FAVORITOS
                BottomNavFavItem(
                    label = "Favoritos",
                    selected = currentRoute == "favorites",
                    badgeCount = favoriteCount,
                    onClick = { onNavigate("favorites") },
                    modifier = Modifier.weight(1f)
                )

                // 5. BUSCAR
                BottomNavItem(
                    label = "Buscar",
                    selected = currentRoute == "search",
                    selectedIcon = Icons.Filled.Search,
                    unselectedIcon = Icons.Outlined.Search,
                    onClick = { onNavigate("search") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconColor = if (selected) BluePrimary else TextMuted
    val textColor = if (selected) BluePrimary else TextMuted
    val fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium

    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (selected) selectedIcon else unselectedIcon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(19.dp)
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = label,
            fontSize = 8.5.sp,
            fontWeight = fontWeight,
            color = textColor,
            maxLines = 1
        )
    }
}

@Composable
private fun BottomNavFavItem(
    label: String,
    selected: Boolean,
    badgeCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconColor = if (selected) BrandRed else TextMuted
    val textColor = if (selected) BrandRed else TextMuted
    val fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium

    val badgeScale by animateFloatAsState(
        targetValue = if (badgeCount > 0) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "favBadgeScale"
    )

    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = if (selected) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier
                    .size(19.dp)
                    .padding(end = if (badgeCount > 0) 1.dp else 0.dp)
            )

            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .offset(x = 6.dp, y = (-2).dp)
                        .scale(badgeScale)
                        .clip(CircleShape)
                        .background(BrandRed)
                        .padding(horizontal = 3.dp, vertical = 0.5.dp)
                        .testTag("bottom_bar_fav_count"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                        color = PureWhite,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(1.dp))

        Text(
            text = label,
            fontSize = 8.5.sp,
            fontWeight = fontWeight,
            color = textColor,
            maxLines = 1
        )
    }
}
