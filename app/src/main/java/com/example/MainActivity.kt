package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CategoryItem
import com.example.data.model.ContentItem
import com.example.ui.components.AppBottomBar
import com.example.ui.components.SideMenuDrawer
import com.example.ui.screens.AllCategoriesScreen
import com.example.ui.screens.AllEpisodesScreen
import com.example.ui.screens.AllMoviesScreen
import com.example.ui.screens.AllUpcomingScreen
import com.example.ui.screens.CategoryDetailScreen
import com.example.ui.screens.ContentDetailScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TrendingScreen
import com.example.ui.screens.WatchScreen
import com.example.ui.screens.WatchlistScreen
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.BrandRed
import com.example.ui.theme.EnpantallaTheme
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EnpantallaTheme {
                EnpantallaApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun EnpantallaApp(viewModel: MainViewModel) {
    val isSplashFinished by viewModel.isSplashFinished.collectAsState()

    if (!isSplashFinished) {
        SplashScreen(
            onSplashComplete = {
                viewModel.finishSplash()
            }
        )
    } else {
        MainAppContent(viewModel = viewModel)
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val activity = androidx.compose.ui.platform.LocalContext.current as? android.app.Activity

    var currentScreen by remember { mutableStateOf("home") }
    var screenHistory by remember { mutableStateOf(listOf("home")) }
    var showSupportAd by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var pendingInterstitialItem by remember { mutableStateOf<ContentItem?>(null) }

    fun navigateTo(screen: String) {
        if (screen == "support_ad") {
            showSupportAd = true
            return
        }
        if (screen != currentScreen) {
            screenHistory = screenHistory + screen
            currentScreen = screen
        }
    }

    fun navigateBack() {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else if (screenHistory.size > 1) {
            val newHistory = screenHistory.dropLast(1)
            screenHistory = newHistory
            currentScreen = newHistory.last()
        } else if (currentScreen != "home") {
            currentScreen = "home"
            screenHistory = listOf("home")
        } else {
            showExitDialog = true
        }
    }

    BackHandler(enabled = true) {
        navigateBack()
    }

    // Collect ViewModel states
    val isLoadingHome by viewModel.isLoadingHome.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val heroItems by viewModel.heroItems.collectAsState()
    val trendingItems by viewModel.trendingItems.collectAsState()
    val latestEpisodes by viewModel.latestEpisodes.collectAsState()
    val upcomingItems by viewModel.upcomingItems.collectAsState()
    val categories by viewModel.categories.collectAsState()

    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categoryPosts by viewModel.categoryPosts.collectAsState()
    val isLoadingCategory by viewModel.isLoadingCategory.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    val currentDetailItem by viewModel.currentDetailItem.collectAsState()
    val relatedEpisodes by viewModel.relatedEpisodes.collectAsState()

    val allEpisodes by viewModel.allEpisodes.collectAsState()
    val allEpisodesPage by viewModel.allEpisodesPage.collectAsState()
    val isLoadingAllEpisodes by viewModel.isLoadingAllEpisodes.collectAsState()

    val paginatedCategories by viewModel.paginatedCategories.collectAsState()
    val allCategoriesPage by viewModel.allCategoriesPage.collectAsState()
    val isLoadingAllCategories by viewModel.isLoadingAllCategories.collectAsState()

    val favorites by viewModel.favorites.collectAsState()
    val watchlist by viewModel.watchlist.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadNotifCount by viewModel.unreadNotifCount.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val movieItems by viewModel.movieItems.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                SideMenuDrawer(
                    currentRoute = currentScreen,
                    onNavigate = { route ->
                        when (route) {
                            "home" -> navigateTo("home")
                            "trending" -> navigateTo("all_trending")
                            "latest" -> {
                                viewModel.loadAllEpisodes(1)
                                navigateTo("all_episodes")
                            }
                            "categories" -> {
                                viewModel.loadAllCategories(1)
                                navigateTo("all_categories")
                            }
                            "movies" -> navigateTo("all_movies")
                            "search" -> navigateTo("search")
                            "favorites" -> navigateTo("favorites")
                            "watchlist" -> navigateTo("watchlist")
                            "notifications" -> navigateTo("notifications")
                            "settings" -> navigateTo("settings")
                        }
                    },
                    onClose = {
                        coroutineScope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
        ) {
            when (currentScreen) {
                "home" -> {
                    HomeScreen(
                        isLoading = isLoadingHome,
                        isRefreshing = isRefreshing,
                        errorMessage = errorMessage,
                        trendingItems = trendingItems,
                        latestEpisodes = latestEpisodes,
                        categories = categories,
                        upcomingItems = upcomingItems,
                        movieItems = movieItems,
                        unreadNotifCount = unreadNotifCount,
                        onMenuClick = {
                            coroutineScope.launch { drawerState.open() }
                        },
                        onSearchClick = {
                            navigateTo("search")
                        },
                        onNotificationsClick = {
                            navigateTo("notifications")
                        },
                        onContentClick = { item ->
                            pendingInterstitialItem = item
                        },
                        onWatchClick = { item ->
                            pendingInterstitialItem = item
                        },
                        onCategoryClick = { category ->
                            viewModel.selectCategory(category)
                            navigateTo("category_detail")
                        },
                        onViewAllTrendingClick = {
                            navigateTo("all_trending")
                        },
                        onViewAllEpisodesClick = {
                            viewModel.loadAllEpisodes(1)
                            navigateTo("all_episodes")
                        },
                        onViewAllCategoriesClick = {
                            viewModel.loadAllCategories(1)
                            navigateTo("all_categories")
                        },
                        onViewAllUpcomingClick = {
                            navigateTo("all_upcoming")
                        },
                        onViewAllMoviesClick = {
                            navigateTo("all_movies")
                        },
                        onRetry = {
                            viewModel.loadHomeData(forceRefresh = true)
                        },
                        onNavigate = { route ->
                            when (route) {
                                "home" -> navigateTo("home")
                                "trending" -> navigateTo("all_trending")
                                "latest" -> {
                                    viewModel.loadAllEpisodes(1)
                                    navigateTo("all_episodes")
                                }
                                "categories" -> {
                                    viewModel.loadAllCategories(1)
                                    navigateTo("all_categories")
                                }
                                "movies" -> navigateTo("all_movies")
                                "search" -> navigateTo("search")
                                "settings" -> navigateTo("settings")
                            }
                        }
                    )
                }

                "all_episodes" -> {
                    AllEpisodesScreen(
                        episodes = if (allEpisodes.isNotEmpty()) allEpisodes else latestEpisodes,
                        currentPage = allEpisodesPage,
                        isLoading = isLoadingAllEpisodes,
                        onPageChange = { viewModel.loadAllEpisodes(it) },
                        onContentClick = { item ->
                            pendingInterstitialItem = item
                        },
                        onBack = { navigateBack() }
                    )
                }

                "category_detail" -> {
                    val currentCat = selectedCategory
                    val catId = "cat_${currentCat?.id ?: 0}"
                    val interaction by viewModel.getInteraction(catId).collectAsState(initial = null)
                    val isFav = currentCat?.let { cat -> favorites.any { it.categoryName.equals(cat.name, ignoreCase = true) } } ?: false

                    CategoryDetailScreen(
                        category = currentCat,
                        posts = categoryPosts,
                        isLoading = isLoadingCategory,
                        interaction = interaction,
                        isFavorite = isFav,
                        onLikeClick = { viewModel.toggleLike(catId) },
                        onDislikeClick = { viewModel.toggleDislike(catId) },
                        onToggleFavorite = {
                            if (categoryPosts.isNotEmpty()) {
                                viewModel.toggleFavorite(categoryPosts.first())
                            }
                        },
                        onBack = { navigateBack() },
                        onContentClick = { item ->
                            pendingInterstitialItem = item
                        }
                    )
                }

                "all_categories" -> {
                    AllCategoriesScreen(
                        categories = if (paginatedCategories.isNotEmpty()) paginatedCategories else categories,
                        currentPage = allCategoriesPage,
                        isLoading = isLoadingAllCategories,
                        onPageChange = { viewModel.loadAllCategories(it) },
                        onCategoryClick = { cat ->
                            viewModel.selectCategory(cat)
                            navigateTo("category_detail")
                        },
                        onBack = { navigateBack() }
                    )
                }

                "search" -> {
                    SearchScreen(
                        query = searchQuery,
                        results = searchResults,
                        recentSearches = recentSearches,
                        isSearching = isSearching,
                        onQueryChange = { viewModel.onSearchQueryChange(it) },
                        onSelectRecentSearch = { viewModel.executeSearch(it) },
                        onDeleteRecentSearch = { viewModel.deleteRecentSearch(it) },
                        onClearRecentSearches = { viewModel.clearRecentSearches() },
                        onContentClick = { item ->
                            pendingInterstitialItem = item
                        },
                        onBack = { navigateBack() }
                    )
                }

                "all_trending" -> {
                    TrendingScreen(
                        trendingItems = trendingItems,
                        isLoading = isLoadingHome,
                        onContentClick = { item ->
                            pendingInterstitialItem = item
                        },
                        onBack = { navigateBack() }
                    )
                }

                "all_upcoming" -> {
                    AllUpcomingScreen(
                        upcomingItems = upcomingItems,
                        isLoading = isLoadingHome,
                        onContentClick = { item ->
                            pendingInterstitialItem = item
                        },
                        onBack = { navigateBack() }
                    )
                }

                "all_movies" -> {
                    AllMoviesScreen(
                        movies = movieItems,
                        isLoading = isLoadingHome,
                        onContentClick = { item ->
                            pendingInterstitialItem = item
                        },
                        onBack = { navigateBack() }
                    )
                }

                "detail" -> {
                    val isFav = currentDetailItem?.let { viewModel.isFavorite(it.id) } ?: false
                    val isWatch = currentDetailItem?.let { viewModel.isWatchlist(it.id) } ?: false

                    ContentDetailScreen(
                        item = currentDetailItem,
                        isFavorite = isFav,
                        isWatchlist = isWatch,
                        relatedEpisodes = relatedEpisodes,
                        onBack = { navigateBack() },
                        onWatchClick = { item ->
                            pendingInterstitialItem = item
                        },
                        onToggleFavorite = { item ->
                            viewModel.toggleFavorite(item)
                        },
                        onToggleWatchlist = { item ->
                            viewModel.toggleWatchlist(item)
                        },
                        onRelatedItemClick = { item ->
                            pendingInterstitialItem = item
                        }
                    )
                }

                "watch" -> {
                    val currentItem = currentDetailItem
                    val itemId = "post_${currentItem?.id ?: 0}"
                    val interaction by viewModel.getInteraction(itemId).collectAsState(initial = null)
                    val isFav = currentItem?.let { viewModel.isFavorite(it.id) } ?: false
                    val isHdUnlocked by viewModel.isHdUnlocked.collectAsState()

                    WatchScreen(
                        item = currentItem,
                        relatedEpisodes = relatedEpisodes,
                        popularCategories = categories,
                        popularMovies = movieItems,
                        interaction = interaction,
                        isFavorite = isFav,
                        isHdUnlocked = isHdUnlocked,
                        onLikeClick = { viewModel.toggleLike(itemId) },
                        onDislikeClick = { viewModel.toggleDislike(itemId) },
                        onToggleFavorite = { currentItem?.let { viewModel.toggleFavorite(it) } },
                        onUnlockHd = { viewModel.unlockHd() },
                        onBack = { navigateBack() },
                        onSelectEpisode = { item ->
                            viewModel.selectDetail(item)
                        },
                        onSelectCategory = { cat ->
                            viewModel.selectCategory(cat)
                            navigateTo("category_detail")
                        },
                        onNavigateHome = {
                            navigateTo("home")
                        }
                    )
                }

                "favorites" -> {
                    FavoritesScreen(
                        favorites = favorites,
                        onBack = { navigateBack() },
                        onContentClick = { id ->
                            viewModel.selectDetailById(id)
                            navigateTo("watch")
                        }
                    )
                }

                "watchlist" -> {
                    WatchlistScreen(
                        watchlist = watchlist,
                        onBack = { navigateBack() },
                        onContentClick = { id ->
                            viewModel.selectDetailById(id)
                            navigateTo("watch")
                        }
                    )
                }

                "notifications" -> {
                    NotificationsScreen(
                        notifications = notifications,
                        onBack = { navigateBack() },
                        onNotificationClick = { postId ->
                            if (postId != null) {
                                viewModel.selectDetailById(postId)
                                navigateTo("watch")
                            }
                        },
                        onMarkAllRead = {
                            viewModel.markAllNotificationsRead()
                        }
                    )
                }

                "profile" -> {
                    FavoritesScreen(
                        favorites = favorites,
                        onBack = { navigateBack() },
                        onContentClick = { id ->
                            viewModel.selectDetailById(id)
                            navigateTo("watch")
                        }
                    )
                }

                "settings" -> {
                    SettingsScreen(
                        onBack = { navigateBack() },
                        onNavigatePrivacyPolicy = { navigateTo("privacy_policy") }
                    )
                }

                "privacy_policy" -> {
                    com.example.ui.screens.PrivacyPolicyScreen(
                        onBack = { navigateBack() }
                    )
                }
            }

        if (currentScreen != "profile" && currentScreen != "privacy_policy") { // Show on standard screens
            AppBottomBar(
                currentRoute = currentScreen,
                favoriteCount = favorites.size,
                onNavigate = { route ->
                    when (route) {
                        "home" -> navigateTo("home")
                        "all_categories" -> {
                            viewModel.loadAllCategories(1)
                            navigateTo("all_categories")
                        }
                        "search" -> navigateTo("search")
                        "all_episodes" -> {
                            viewModel.loadAllEpisodes(1)
                            navigateTo("all_episodes")
                        }
                        "all_movies" -> navigateTo("all_movies")
                        "favorites" -> navigateTo("favorites")
                        else -> navigateTo(route)
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        if (showSupportAd) {
            com.example.ui.components.RewardedEpisodeAdDialog(
                episodeTitle = "Apoyar Aplicación",
                onRewardEarned = {
                    showSupportAd = false
                    viewModel.unlockHd()
                }
            )
        }

        if (showExitDialog) {
            Dialog(onDismissRequest = { showExitDialog = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PureWhite,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_round_play),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "¿Salir de la aplicación?",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "¿Estás seguro de que deseas salir de Ennovelas?",
                            fontSize = 13.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showExitDialog = false },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Cancelar", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = {
                                    showExitDialog = false
                                    activity?.finish()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BrandRed,
                                    contentColor = PureWhite
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Salir", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Interstitial Ad Dialog on Post Click from Home
        val currentInterstitial = pendingInterstitialItem
        if (currentInterstitial != null) {
            com.example.ui.components.InterstitialAdDialog(
                onAdClosed = {
                    val target = pendingInterstitialItem
                    pendingInterstitialItem = null
                    if (target != null) {
                        viewModel.selectDetail(target)
                        navigateTo("watch")
                    }
                }
            )
        }
    }
}
}
