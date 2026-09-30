package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WpPostResponse(
    @Json(name = "id") val id: Int,
    @Json(name = "date") val date: String? = null,
    @Json(name = "slug") val slug: String? = null,
    @Json(name = "title") val title: RenderedText? = null,
    @Json(name = "content") val content: RenderedText? = null,
    @Json(name = "excerpt") val excerpt: RenderedText? = null,
    @Json(name = "categories") val categories: List<Int>? = null,
    @Json(name = "featured_media") val featuredMedia: Int? = null,
    @Json(name = "_embedded") val embedded: WpEmbedded? = null,
    @Json(name = "yoast_head_json") val yoastHead: WpYoastHead? = null
)

@JsonClass(generateAdapter = true)
data class RenderedText(
    @Json(name = "rendered") val rendered: String? = null
)

@JsonClass(generateAdapter = true)
data class WpEmbedded(
    @Json(name = "wp:featuredmedia") val wpFeaturedMedia: List<WpMediaItem>? = null,
    @Json(name = "wp:term") val wpTerm: List<List<WpTermItem>>? = null
)

@JsonClass(generateAdapter = true)
data class WpMediaItem(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "source_url") val sourceUrl: String? = null,
    @Json(name = "alt_text") val altText: String? = null
)

@JsonClass(generateAdapter = true)
data class WpTermItem(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "slug") val slug: String? = null,
    @Json(name = "taxonomy") val taxonomy: String? = null
)

@JsonClass(generateAdapter = true)
data class WpYoastHead(
    @Json(name = "title") val title: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "og_image") val ogImage: List<WpOgImage>? = null
)

@JsonClass(generateAdapter = true)
data class WpOgImage(
    @Json(name = "url") val url: String? = null,
    @Json(name = "width") val width: Int? = null,
    @Json(name = "height") val height: Int? = null
)

@JsonClass(generateAdapter = true)
data class WpCategoryResponse(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "count") val count: Int = 0,
    @Json(name = "slug") val slug: String = "",
    @Json(name = "description") val description: String? = null,
    @Json(name = "yoast_head_json") val yoastHead: WpYoastHead? = null
)

// Domain Models for UI consumption
data class ContentItem(
    val id: Int,
    val title: String,
    val rawTitle: String,
    val categoryId: Int,
    val categoryName: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val videoEmbedUrl: String?,
    val episodeBadge: String?,
    val year: String?,
    val quality: String = "HD",
    val rating: String? = null,
    val description: String,
    val rawDate: String,
    val imdbRating: String? = null,
    val genre: String? = null,
    val releaseYear: String? = null,
    val shortDescription: String? = null,
    val sliderImageUrl: String? = null,
    val isUpcoming: Boolean = false,
    val upcomingDateBadge: String? = null,
    val isTrending: Boolean = false,
    val isMovie: Boolean = false
)

data class CategoryItem(
    val id: Int,
    val name: String,
    val count: Int,
    val slug: String,
    val description: String?,
    val sampleImageUrl: String?,
    val imdbRating: String? = null,
    val genre: String? = null,
    val releaseYear: String? = null,
    val shortDescription: String? = null,
    val sliderImageUrl: String? = null,
    val isMovieCategory: Boolean = false
)
