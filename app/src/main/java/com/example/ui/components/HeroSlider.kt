package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.model.CategoryItem
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.PureWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HeroSlider(
    categories: List<CategoryItem>,
    onCategoryClick: (CategoryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (categories.isEmpty()) return

    val displayItems = categories.take(6)
    val pagerState = rememberPagerState(pageCount = { displayItems.size })
    val coroutineScope = rememberCoroutineScope()

    // Smooth auto-slide every 5 seconds
    LaunchedEffect(pagerState.pageCount) {
        if (displayItems.size > 1) {
            while (true) {
                delay(5000)
                val nextPage = (pagerState.currentPage + 1) % displayItems.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    // Height 270.dp to elegantly fit title, custom fields & short description
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(270.dp)
            .testTag("hero_slider")
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val cat = displayItems[page]
            CategoryHeroSlide(
                category = cat,
                onClick = { onCategoryClick(cat) }
            )
        }

        // Small pagination indicators (dots)
        if (displayItems.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                displayItems.indices.forEach { index ->
                    val isSelected = pagerState.currentPage == index
                    val width by animateFloatAsState(
                        targetValue = if (isSelected) 16f else 5f,
                        animationSpec = tween(durationMillis = 200),
                        label = "dotWidth"
                    )

                    Box(
                        modifier = Modifier
                            .height(4.dp)
                            .width(width.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) GreenPrimary else PureWhite.copy(alpha = 0.45f)
                            )
                            .clickable {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryHeroSlide(
    category: CategoryItem,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = tween(100),
        label = "btnScale"
    )

    val ratingText = category.imdbRating ?: "8.4"
    val yearText = category.releaseYear ?: "2024"
    val genreText = category.genre ?: "Novela / Drama"
    val shortDesc = category.shortDescription
        ?: category.description
        ?: "Disfruta de la mejor producción completa en alta definición y audio latino."

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick)
    ) {
        val heroImage = category.sliderImageUrl?.takeIf { it.isNotBlank() } ?: category.sampleImageUrl
        // Backdrop Image
        if (!heroImage.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(heroImage)
                    .crossfade(true)
                    .build(),
                contentDescription = category.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF202020), Color(0xFF101010))
                        )
                    )
            )
        }

        // Clean cinematic gradient overlays
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.45f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.75f),
                            Color.Black.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.85f),
                            Color.Black.copy(alpha = 0.40f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Content on the hero slide
        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .align(Alignment.BottomStart)
                .padding(start = 14.dp, end = 14.dp, bottom = 12.dp)
        ) {
            // Badges Row: IMDb Rating, Year, Genre, Count, HD (Compact height)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                // IMDb Rating Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.5.dp))
                        .background(Color(0xFFF5C518)) // Official IMDb Gold/Yellow
                        .padding(horizontal = 3.dp, vertical = 0.5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(7.5.dp)
                        )
                        Spacer(modifier = Modifier.width(1.5.dp))
                        Text(
                            text = "IMDb $ratingText",
                            color = Color.Black,
                            fontSize = 6.8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Release Year Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.5.dp))
                        .background(PureWhite.copy(alpha = 0.22f))
                        .padding(horizontal = 3.dp, vertical = 0.5.dp)
                ) {
                    Text(
                        text = yearText,
                        color = PureWhite,
                        fontSize = 6.8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Genre Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.5.dp))
                        .background(GreenPrimary)
                        .padding(horizontal = 3.dp, vertical = 0.5.dp)
                ) {
                    Text(
                        text = genreText.uppercase(),
                        color = PureWhite,
                        fontSize = 6.8.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Capítulos Count Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.5.dp))
                        .background(Color.Black.copy(alpha = 0.60f))
                        .padding(horizontal = 3.dp, vertical = 0.5.dp)
                        .height(9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${category.count} CAP",
                        color = PureWhite,
                        fontSize = 6.8.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 7.sp
                    )
                }

                // HD Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.5.dp))
                        .background(Color.Black.copy(alpha = 0.70f))
                        .padding(horizontal = 2.5.dp, vertical = 0.5.dp)
                ) {
                    Text(
                        text = "HD",
                        color = GreenPrimary,
                        fontSize = 6.5.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Cinematic Category / Series Title
            Text(
                text = category.name,
                color = PureWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Short Description from custom field / category description
            Text(
                text = shortDesc,
                color = PureWhite.copy(alpha = 0.85f),
                fontSize = 10.sp,
                lineHeight = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(5.dp))

            // Compact Button: VER CAPÍTULOS
            Button(
                onClick = onClick,
                interactionSource = interactionSource,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    contentColor = PureWhite
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 1.dp
                ),
                modifier = Modifier
                    .scale(buttonScale)
                    .height(22.dp)
                    .testTag("hero_watch_now_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(2.5.dp))
                    Text(
                        text = "VER CAPÍTULOS",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.2.sp
                    )
                }
            }
        }
    }
}
