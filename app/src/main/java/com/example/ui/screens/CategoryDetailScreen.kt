package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.PostInteractionEntity
import com.example.data.model.CategoryItem
import com.example.data.model.ContentItem
import com.example.ui.components.InteractionBar
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.BorderLight
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryDetailScreen(
    category: CategoryItem?,
    posts: List<ContentItem>,
    isLoading: Boolean,
    interaction: PostInteractionEntity?,
    isFavorite: Boolean,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onBack: () -> Unit,
    onContentClick: (ContentItem) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .statusBarsPadding()
            .testTag("category_detail_screen")
    ) {
        // Top Back Header
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
                    .testTag("category_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = category?.name ?: "Categoría",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 130.dp)
        ) {
            // 1. CATEGORY IMAGE WITH TITLE IN BOTTOM CORNER & 3D BLACK SHADOW
            val headerImage = category?.sliderImageUrl?.takeIf { it.isNotBlank() } ?: category?.sampleImageUrl
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp), // 3D black shadow
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9.5f)
                    .testTag("category_header_card")
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (!headerImage.isNullOrBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(headerImage)
                                .crossfade(true)
                                .build(),
                            contentDescription = category?.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // ONLY subtle bottom shadow so title is crisp while rest is clear
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colorStops = arrayOf(
                                            0.0f to Color.Transparent,
                                            0.55f to Color.Transparent,
                                            0.80f to Color.Black.copy(alpha = 0.50f),
                                            1.0f to Color.Black.copy(alpha = 0.90f)
                                        )
                                    )
                                )
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF212121)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = null,
                                tint = GreenPrimary,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    // Top episode count tag (Ultra Compact)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(2.5.dp))
                            .background(GreenPrimary)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "${posts.size.coerceAtLeast(category?.count ?: 0)} CAP",
                            color = PureWhite,
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Bottom title directly inside bottom corner
                    Text(
                        text = category?.name ?: "",
                        color = PureWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 22.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. INTERACTION BUTTONS: LIKE, DISLIKE, FAVORITE, SHARE
            val catId = "cat_${category?.id ?: 0}"
            InteractionBar(
                interaction = interaction,
                isFavorite = isFavorite,
                title = category?.name ?: "Serie",
                shareUrl = "https://enpantallatv.me/?cat=${category?.id ?: 0}",
                onLikeClick = onLikeClick,
                onDislikeClick = onDislikeClick,
                onFavoriteClick = onToggleFavorite
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3. ALL EPISODES OF THIS CATEGORY IN CAP BADGE FORMAT (NO BULKY IMAGES)
            Text(
                text = "Todos los Capítulos",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            Text(
                text = "Toca un capítulo para empezar a ver",
                fontSize = 11.5.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (isLoading && posts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GreenPrimary)
                }
            } else if (posts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron capítulos en esta categoría.",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }
            } else {
                // 5 Cap Buttons per row with uniform fixed height and clean alignment
                val chunkedPosts = posts.chunked(5)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    chunkedPosts.forEachIndexed { rowIndex, rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowItems.forEachIndexed { colIndex, item ->
                                val globalIndex = (rowIndex * 5) + colIndex
                                val badgeLabel = if (item.isUpcoming) {
                                    item.upcomingDateBadge ?: "Estreno"
                                } else {
                                    item.episodeBadge ?: "Cap. ${posts.size - globalIndex}"
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .shadow(1.dp, RoundedCornerShape(6.dp))
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(PureWhite)
                                        .border(1.dp, BorderLight, RoundedCornerShape(6.dp))
                                        .clickable { onContentClick(item) }
                                        .padding(horizontal = 2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = badgeLabel,
                                        fontSize = if (badgeLabel.length > 7) 8.5.sp else 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
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

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
