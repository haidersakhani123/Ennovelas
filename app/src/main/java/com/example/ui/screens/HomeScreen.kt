package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryItem
import com.example.data.model.ContentItem
import com.example.ui.components.AdMobBannerAd
import com.example.ui.components.AppHeader
import com.example.ui.components.CategorySection
import com.example.ui.components.HeroSkeleton
import com.example.ui.components.HeroSlider
import com.example.ui.components.HomeSkeletonScreen
import com.example.ui.components.LatestEpisodesSection
import com.example.ui.components.MoviesSection
import com.example.ui.components.TrendingSection
import com.example.ui.components.UpcomingSection
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.BorderLight
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isLoading: Boolean,
    isRefreshing: Boolean = false,
    errorMessage: String?,
    trendingItems: List<ContentItem> = emptyList(),
    latestEpisodes: List<ContentItem>,
    categories: List<CategoryItem>,
    upcomingItems: List<ContentItem> = emptyList(),
    movieItems: List<ContentItem> = emptyList(),
    unreadNotifCount: Int,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onContentClick: (ContentItem) -> Unit,
    onWatchClick: (ContentItem) -> Unit,
    onCategoryClick: (CategoryItem) -> Unit,
    onViewAllTrendingClick: () -> Unit = {},
    onViewAllEpisodesClick: () -> Unit,
    onViewAllCategoriesClick: () -> Unit,
    onViewAllUpcomingClick: () -> Unit = {},
    onViewAllMoviesClick: () -> Unit = {},
    onRetry: () -> Unit,
    onRefresh: () -> Unit = onRetry,
    onNavigate: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val isScrolled by remember {
        derivedStateOf { scrollState.value > 120 }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .testTag("home_screen")
    ) {
        if (isLoading && latestEpisodes.isEmpty() && categories.isEmpty()) {
            HomeSkeletonScreen()
        } else if (errorMessage != null && latestEpisodes.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Text(
                    text = errorMessage,
                    fontSize = 15.sp,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Comprueba tu conexión o intenta nuevamente.",
                    fontSize = 13.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary,
                        contentColor = PureWhite
                    )
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Reintentar", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // Pull To Refresh Box
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(bottom = 60.dp) // Tight bottom padding for floating bar
                ) {
                    // 1. HERO SLIDER
                    HeroSlider(
                        categories = categories,
                        onCategoryClick = onCategoryClick
                    )

                    // AD 1: Banner Ad under slider
                    AdMobBannerAd(
                        adUnitName = "Banner Slider",
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )

                    // 2. TRENDING SECTION (At top with Fire badge)
                    if (trendingItems.isNotEmpty()) {
                        TrendingSection(
                            items = trendingItems,
                            onItemClick = onContentClick,
                            onViewAllClick = onViewAllTrendingClick
                        )

                        AdMobBannerAd(
                            adUnitName = "Banner Tendencias",
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }

                    // 3. LATEST EPISODES SECTION (Horizontal swipe)
                    LatestEpisodesSection(
                        items = latestEpisodes,
                        onItemClick = onContentClick,
                        onViewAllClick = onViewAllEpisodesClick
                    )

                    // AD 2: Banner Ad under Latest Posts
                    AdMobBannerAd(
                        adUnitName = "Banner Episodios",
                        modifier = Modifier.padding(vertical = 2.dp)
                    )

                    // 4. LATEST CATEGORIES SECTION (Horizontal swipe)
                    CategorySection(
                        categories = categories,
                        onCategoryClick = onCategoryClick,
                        onViewAllClick = onViewAllCategoriesClick
                    )

                    // AD 3: Banner Ad under Latest Categories
                    if (upcomingItems.isNotEmpty()) {
                        AdMobBannerAd(
                            adUnitName = "Banner Próximos",
                            modifier = Modifier.padding(vertical = 2.dp)
                        )

                        // 5. UPCOMING RELEASES SECTION (Horizontal swipe)
                        UpcomingSection(
                            items = upcomingItems,
                            onItemClick = onContentClick,
                            onViewAllClick = onViewAllUpcomingClick
                        )
                    }

                    // AD 4: Banner Ad under Upcoming Section
                    if (movieItems.isNotEmpty()) {
                        AdMobBannerAd(
                            adUnitName = "Banner Películas",
                            modifier = Modifier.padding(vertical = 2.dp)
                        )

                        // 6. MOVIES SECTION (Horizontal swipe)
                        MoviesSection(
                            items = movieItems,
                            onItemClick = onContentClick,
                            onViewAllClick = onViewAllMoviesClick
                        )
                    }
                }
            }
        }

        // FLOATING OVERLAY HEADER
        AppHeader(
            isScrolled = isScrolled,
            unreadNotifCount = unreadNotifCount,
            onMenuClick = onMenuClick,
            onSearchClick = onSearchClick,
            onNotificationsClick = onNotificationsClick,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
