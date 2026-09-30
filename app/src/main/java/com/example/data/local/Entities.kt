package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val categoryName: String,
    val posterUrl: String?,
    val videoUrl: String?,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val categoryName: String,
    val posterUrl: String?,
    val videoUrl: String?,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val username: String,
    val email: String,
    val avatarUrl: String = "",
    val isLoggedIn: Boolean = false,
    val joinedDate: String = "2026"
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val postId: Int? = null
)

@Entity(tableName = "post_interactions")
data class PostInteractionEntity(
    @PrimaryKey val id: String, // e.g. "post_123" or "cat_45"
    val likesCount: Int = 12,
    val dislikesCount: Int = 0,
    val userAction: Int = 0 // 1 = liked, -1 = disliked, 0 = none
)

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey val query: String,
    val timestamp: Long = System.currentTimeMillis()
)

