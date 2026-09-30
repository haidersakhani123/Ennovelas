package com.example.ui.screens

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.PostInteractionEntity
import com.example.data.model.CategoryItem
import com.example.data.model.ContentItem
import com.example.ui.components.InteractionBar
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BrandRed
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenLight
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WatchScreen(
    item: ContentItem?,
    relatedEpisodes: List<ContentItem>,
    popularCategories: List<CategoryItem>,
    popularMovies: List<ContentItem> = emptyList(),
    interaction: PostInteractionEntity?,
    isFavorite: Boolean,
    isHdUnlocked: Boolean,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onUnlockHd: () -> Unit,
    onBack: () -> Unit,
    onSelectEpisode: (ContentItem) -> Unit,
    onSelectCategory: (CategoryItem) -> Unit,
    onNavigateHome: () -> Unit
) {
    if (item == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = GreenPrimary)
        }
        return
    }

    val scrollState = rememberScrollState()
    var isVideoLoading by remember { mutableStateOf(false) }
    var pendingEpisode by remember { mutableStateOf<ContentItem?>(null) }

    BackHandler {
        onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .statusBarsPadding()
            .testTag("watch_screen")
    ) {
        // 1. BREADCRUMB AT TOP (Inicio > Category > Title)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("watch_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Clickable Breadcrumbs
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                if (item.isMovie) {
                    Text(
                        text = "Películas",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0284C7),
                        modifier = Modifier.clickable(onClick = onNavigateHome)
                    )
                    Text(
                        text = "  ›  ",
                        fontSize = 11.5.sp,
                        color = TextMuted
                    )
                    Text(
                        text = item.title,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(
                        text = "Inicio",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted,
                        modifier = Modifier.clickable(onClick = onNavigateHome)
                    )
                    Text(
                        text = "  ›  ",
                        fontSize = 11.5.sp,
                        color = TextMuted
                    )
                    Text(
                        text = item.categoryName,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable {
                            popularCategories.find { it.id == item.categoryId }?.let { onSelectCategory(it) }
                        }
                    )
                    Text(
                        text = "  ›  ",
                        fontSize = 11.5.sp,
                        color = TextMuted
                    )
                    Text(
                        text = item.episodeBadge ?: "Capítulo",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // 2. VIDEO PLAYER DIRECTLY UNDER BREADCRUMB (16:9 seamless loader)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color(0xFF0F172A)),
            contentAlignment = Alignment.Center
        ) {
            val videoUrl = item.videoEmbedUrl
            val isRealEmbed = !videoUrl.isNullOrBlank() && !videoUrl.contains("?p=")
            val poster = item.sliderImageUrl ?: item.posterUrl ?: item.backdropUrl

            // 1. AndroidView with WebView: Always mounted with transparent background so it never destroys or flashes
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            mediaPlaybackRequiresUserGesture = false
                            allowContentAccess = true
                            allowFileAccess = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            cacheMode = WebSettings.LOAD_DEFAULT
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        }
                        webChromeClient = WebChromeClient()
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                isVideoLoading = true
                            }
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isVideoLoading = false
                            }
                            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean = false
                        }
                        if (isRealEmbed) {
                            tag = videoUrl
                            loadUrl(videoUrl!!)
                        }
                    }
                },
                update = { view ->
                    if (isRealEmbed && view.tag != videoUrl) {
                        view.tag = videoUrl
                        isVideoLoading = true
                        view.loadUrl(videoUrl!!)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // 2. Poster Buffer Layer: Sits ON TOP of WebView while loading or resolving embed
            // Completely masks any native WebView creation, blank frames or black jhatka
            if (!isRealEmbed || isVideoLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (!poster.isNullOrBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(poster)
                                .crossfade(true)
                                .build(),
                            contentDescription = item.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.40f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = GreenPrimary,
                                modifier = Modifier.size(32.dp),
                                strokeWidth = 2.8.dp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (!isRealEmbed) "Preparando capítulo..." else "Cargando video...",
                                color = PureWhite,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Banner Ad under Video
        com.example.ui.components.AdMobBannerAd(adUnitName = "Banner Reproductor")

        // SCROLLABLE BODY UNDER PLAYER
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            // 3. TITLE & BADGES (Category badge, Cap badge, Rewarded Ad HD badge)
            Text(
                text = item.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Category Badge (Ultra Compact)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(GreenPrimary)
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.categoryName,
                        color = PureWhite,
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Cap Badge (Ultra Compact)
                if (item.episodeBadge != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(PureWhite)
                            .border(1.dp, GreenPrimary, RoundedCornerShape(3.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.episodeBadge,
                            color = GreenPrimary,
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // HD Quality Badge (Clean display, no forced rewarded ad)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(BluePrimary)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HighQuality,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "1080p FULL HD",
                            color = PureWhite,
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4. ACTION BUTTONS: LIKE, DISLIKE, FAVORITE, SHARE (Persistent Counts)
            InteractionBar(
                interaction = interaction,
                isFavorite = isFavorite,
                title = item.title,
                shareUrl = "https://enpantallatv.me/?p=${item.id}",
                onLikeClick = onLikeClick,
                onDislikeClick = onDislikeClick,
                onFavoriteClick = onToggleFavorite
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5. ALL EPISODES IN 5 BUTTONS PER ROW (ONLY FOR SERIES, NOT FOR MOVIES)
            if (!item.isMovie) {
                val episodesList = remember(relatedEpisodes, item.id) {
                    (relatedEpisodes + item)
                        .distinctBy { it.id }
                        .sortedBy { it.id }
                }

                if (episodesList.isNotEmpty()) {
                    Text(
                        text = "Capítulos de ${item.categoryName}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Toca cualquier capítulo para reproducirlo",
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Exact 5 Cap Buttons per row
                    val chunkedEpisodes = episodesList.chunked(5)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        chunkedEpisodes.forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                rowItems.forEach { ep ->
                                    val isCurrent = ep.id == item.id
                                    val badgeLabel = ep.episodeBadge ?: "Cap ${ep.id}"
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .shadow(if (isCurrent) 2.dp else 1.dp, RoundedCornerShape(6.dp))
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isCurrent) GreenPrimary else PureWhite)
                                            .border(
                                                width = 1.dp,
                                                color = if (isCurrent) GreenPrimary else BorderLight,
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                            .clickable {
                                                if (!isCurrent) {
                                                    pendingEpisode = ep
                                                }
                                            }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = badgeLabel,
                                            fontSize = 10.sp,
                                            fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Bold,
                                            color = if (isCurrent) PureWhite else TextPrimary,
                                            maxLines = 1
                                        )
                                    }
                                }
                                val emptySlots = 5 - rowItems.size
                                repeat(emptySlots) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 6. POPULAR MOVIES OR POPULAR SERIES
            if (item.isMovie && popularMovies.isNotEmpty()) {
                val otherMovies = popularMovies.filter { it.id != item.id }
                if (otherMovies.isNotEmpty()) {
                    Text(
                        text = "Películas Recomendadas 🎬",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Más películas completas que te pueden gustar",
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(otherMovies, key = { it.id }) { movie ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                modifier = Modifier
                                    .width(125.dp)
                                    .aspectRatio(0.70f)
                                    .clickable { onSelectEpisode(movie) }
                                    .testTag("watch_popular_movie_${movie.id}")
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    val movieImg = movie.sliderImageUrl?.takeIf { it.isNotBlank() } ?: movie.posterUrl
                                    if (!movieImg.isNullOrBlank()) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(movieImg)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = movie.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.verticalGradient(
                                                        colorStops = arrayOf(
                                                            0.0f to Color.Transparent,
                                                            0.65f to Color.Transparent,
                                                            1.0f to Color.Black.copy(alpha = 0.85f)
                                                        )
                                                    )
                                                )
                                        )
                                    }

                                    // Top badge
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(5.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(Color(0xFF0284C7))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "PELÍCULA",
                                            color = PureWhite,
                                            fontSize = 6.5.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }

                                    // Bottom Title
                                    Text(
                                        text = movie.title,
                                        color = PureWhite,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(horizontal = 8.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else if (!item.isMovie && popularCategories.isNotEmpty()) {
                Text(
                    text = "Series y Novelas más Populares",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = "Explora las producciones más vistas en Ennovelas",
                    fontSize = 11.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(popularCategories, key = { it.id }) { cat ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            modifier = Modifier
                                .width(125.dp)
                                .aspectRatio(0.70f)
                                .clickable { onSelectCategory(cat) }
                                .testTag("watch_popular_cat_${cat.id}")
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                val catImg = cat.sampleImageUrl?.takeIf { it.isNotBlank() } ?: cat.sliderImageUrl
                                if (!catImg.isNullOrBlank()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(catImg)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = cat.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    // Subtle bottom gradient for title
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    colorStops = arrayOf(
                                                        0.0f to Color.Transparent,
                                                        0.65f to Color.Transparent,
                                                        1.0f to Color.Black.copy(alpha = 0.85f)
                                                    )
                                                )
                                            )
                                    )
                                }

                                // Top count badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(5.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(GreenPrimary)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "${cat.count} CAP",
                                        color = PureWhite,
                                        fontSize = 7.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Bottom Title
                                Text(
                                    text = cat.name,
                                    color = PureWhite,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 13.sp,
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(6.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(96.dp))
        }

        // Automatic Rewarded Ad on Episode Click
        val currentPendingEp = pendingEpisode
        if (currentPendingEp != null) {
            com.example.ui.components.RewardedEpisodeAdDialog(
                episodeTitle = currentPendingEp.episodeBadge?.let { "$it - ${currentPendingEp.title}" } ?: currentPendingEp.title,
                onRewardEarned = {
                    pendingEpisode = null
                    isVideoLoading = true
                    onSelectEpisode(currentPendingEp)
                }
            )
        }
    }
}
