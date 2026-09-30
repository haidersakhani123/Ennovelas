package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PostInteractionEntity
import com.example.ui.theme.BrandRed
import com.example.ui.theme.GreenLight
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextPrimary

@Composable
fun InteractionBar(
    interaction: PostInteractionEntity?,
    isFavorite: Boolean,
    title: String,
    shareUrl: String,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val likesCount = interaction?.likesCount ?: 18
    val dislikesCount = interaction?.dislikesCount ?: 0
    val userAction = interaction?.userAction ?: 0 // 1 = like, -1 = dislike, 0 = none

    val isLiked = userAction == 1
    val isDisliked = userAction == -1

    val likeColor by animateColorAsState(
        targetValue = if (isLiked) GreenPrimary else TextPrimary,
        label = "likeColor"
    )
    val dislikeColor by animateColorAsState(
        targetValue = if (isDisliked) BrandRed else TextPrimary,
        label = "dislikeColor"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. LIKE BUTTON (Barabar Size)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .shadow(2.dp, RoundedCornerShape(19.dp))
                .clip(RoundedCornerShape(19.dp))
                .background(if (isLiked) GreenLight else PureWhite)
                .clickable(onClick = onLikeClick)
                .padding(horizontal = 6.dp)
                .testTag("interaction_like_button"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                    contentDescription = "Me gusta",
                    tint = likeColor,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = likesCount.toString(),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = likeColor,
                    maxLines = 1
                )
            }
        }

        // 2. DISLIKE BUTTON (Barabar Size)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .shadow(2.dp, RoundedCornerShape(19.dp))
                .clip(RoundedCornerShape(19.dp))
                .background(if (isDisliked) BrandRed.copy(alpha = 0.12f) else PureWhite)
                .clickable(onClick = onDislikeClick)
                .padding(horizontal = 6.dp)
                .testTag("interaction_dislike_button"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isDisliked) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                    contentDescription = "No me gusta",
                    tint = dislikeColor,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = dislikesCount.toString(),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = dislikeColor,
                    maxLines = 1
                )
            }
        }

        // 3. FAVORITE BUTTON (Barabar Size)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .shadow(2.dp, RoundedCornerShape(19.dp))
                .clip(RoundedCornerShape(19.dp))
                .background(if (isFavorite) BrandRed.copy(alpha = 0.12f) else PureWhite)
                .clickable(onClick = onFavoriteClick)
                .padding(horizontal = 6.dp)
                .testTag("interaction_fav_button"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (isFavorite) BrandRed else TextPrimary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isFavorite) "Guardado" else "Favorito",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isFavorite) BrandRed else TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // 4. SHARE BUTTON (Barabar Size)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .shadow(2.dp, RoundedCornerShape(19.dp))
                .clip(RoundedCornerShape(19.dp))
                .background(PureWhite)
                .clickable {
                    shareContent(context, title, shareUrl)
                }
                .padding(horizontal = 6.dp)
                .testTag("interaction_share_button"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Compartir",
                    tint = TextPrimary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Compartir",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun shareContent(context: Context, title: String, url: String) {
    try {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Mira $title en Ennovelas: $url")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Compartir en:")
        context.startActivity(shareIntent)
    } catch (_: Exception) {}
}
