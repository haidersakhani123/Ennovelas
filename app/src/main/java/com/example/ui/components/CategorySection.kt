package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun CategorySection(
    categories: List<CategoryItem>,
    onCategoryClick: (CategoryItem) -> Unit,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (categories.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .testTag("categories_section")
    ) {
        // Section Header with compact "Ver más"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Últimas Categorías",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = "Desliza para explorar series y novelas",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            // Compact "Ver más" button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(GreenPrimary.copy(alpha = 0.12f))
                    .clickable(onClick = onViewAllClick)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                    .testTag("categories_ver_mas")
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

        // Horizontal Swipe / Slide Navigation (Matching Latest Episodes & Popular Watch format)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 14.dp)
        ) {
            items(categories, key = { it.id }) { cat ->
                CategoryGridCard(
                    category = cat,
                    onClick = { onCategoryClick(cat) },
                    modifier = Modifier.width(115.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryGridCard(
    category: CategoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 1.dp
        ),
        modifier = modifier
            .aspectRatio(0.70f)
            .clickable(onClick = onClick)
            .testTag("category_card_${category.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E293B),
                            Color(0xFF0F172A)
                        )
                    )
                )
        ) {
            val catImage = category.sampleImageUrl?.takeIf { it.isNotBlank() } ?: category.sliderImageUrl
            // Background image: completely clear and crisp
            if (!catImage.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(catImage)
                        .crossfade(true)
                        .build(),
                    contentDescription = category.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Elegant fallback with watermark play icon
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AppPlayIcon(size = 32.dp)
                }
            }

            // ONLY subtle bottom shadow behind title text
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.0f to Color.Transparent,
                                0.55f to Color.Transparent,
                                0.80f to Color.Black.copy(alpha = 0.50f),
                                1.0f to Color.Black.copy(alpha = 0.88f)
                            )
                        )
                    )
            )

            // Top episode count tag (Ultra compact)
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
                    .height(9.5.dp)
                    .clip(RoundedCornerShape(2.5.dp))
                    .background(GreenPrimary)
                    .padding(horizontal = 3.5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${category.count} CAP",
                    color = PureWhite,
                    fontSize = 6.2.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 7.sp
                )
            }

            // Bottom Title
            Text(
                text = category.name,
                color = PureWhite,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 13.5.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 6.dp, vertical = 6.dp)
            )
        }
    }
}
