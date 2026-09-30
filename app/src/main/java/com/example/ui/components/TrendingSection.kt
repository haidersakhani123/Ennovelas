package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.ContentItem
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun TrendingSection(
    items: List<ContentItem>,
    onItemClick: (ContentItem) -> Unit,
    onViewAllClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .testTag("trending_section")
    ) {
        // Section Header with Green "Ver más →" button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Tendencias de Hoy 🔥",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                }
                Text(
                    text = "Top 10 más populares ahora",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            // Compact "Ver más" Green badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(GreenPrimary.copy(alpha = 0.12f))
                    .clickable(onClick = onViewAllClick)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                    .testTag("trending_ver_mas")
            ) {
                Text(
                    text = "Ver más →",
                    color = GreenDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Horizontal Swipe Netflix Style with 3D Ranking Numbers
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 14.dp)
        ) {
            itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
                TrendingCard(
                    rank = index + 1,
                    item = item,
                    onClick = { onItemClick(item) },
                    modifier = Modifier.width(148.dp)
                )
            }
        }
    }
}

@Composable
fun TrendingCard(
    rank: Int,
    item: ContentItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = tween(120),
        label = "cardScale"
    )

    // Animated Burning Flame Icon
    val infiniteTransition = rememberInfiniteTransition(label = "flamePulse")
    val flameScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clickable(onClick = onClick)
            .testTag("trending_card_${item.id}"),
        contentAlignment = Alignment.BottomStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            // Netflix-Style 3D Large Rank Number
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .padding(bottom = 2.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                // 3D Shadow depth
                Text(
                    text = "$rank",
                    fontSize = 58.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black.copy(alpha = 0.40f),
                    lineHeight = 56.sp,
                    modifier = Modifier.offset(x = 2.dp, y = 2.dp)
                )
                // 3D Metallic chrome number
                Text(
                    text = "$rank",
                    fontSize = 58.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF64748B),
                    lineHeight = 56.sp
                )
            }

            // Poster Card with 3D shadow
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 1.dp
                ),
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(0.70f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1E293B),
                                    Color(0xFF0F172A)
                                )
                            )
                        )
                ) {
                    val imgUrl = item.sliderImageUrl?.takeIf { it.isNotBlank() } ?: item.posterUrl
                    if (!imgUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imgUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = item.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            AppPlayIcon(size = 32.dp)
                        }
                    }

                    // Bottom shadow behind title
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colorStops = arrayOf(
                                        0.0f to Color.Transparent,
                                        0.65f to Color.Transparent,
                                        0.85f to Color.Black.copy(alpha = 0.50f),
                                        1.0f to Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )

                    // Top Left: Cap Badge
                    val badgeText = item.episodeBadge ?: "CAP"
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(GreenPrimary)
                            .padding(horizontal = 3.5.dp, vertical = 1.5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = badgeText,
                            color = PureWhite,
                            fontSize = 6.sp,
                            fontWeight = FontWeight.Black,
                            lineHeight = 7.sp
                        )
                    }

                    // Top Right: ONLY Fire Flame Icon 🔥 in animated glowing circle
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .scale(flameScale)
                            .size(17.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFFF3D00),
                                        Color(0xFFFF9100)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔥",
                            fontSize = 9.sp,
                            lineHeight = 10.sp
                        )
                    }

                    // Bottom Title
                    Text(
                        text = item.title,
                        color = PureWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 6.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
