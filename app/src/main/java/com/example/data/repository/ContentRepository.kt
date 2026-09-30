package com.example.data.repository

import android.content.Context
import android.os.Build
import android.text.Html
import com.example.data.api.ApiClient
import com.example.data.api.WordPressApiService
import com.example.data.local.AppDatabase
import com.example.data.local.FavoriteEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.PostInteractionEntity
import com.example.data.local.RecentSearchEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.WatchlistEntity
import com.example.data.model.CategoryItem
import com.example.data.model.ContentItem
import com.example.data.model.WpCategoryResponse
import com.example.data.model.WpPostResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.regex.Pattern

class ContentRepository(context: Context) {

    private val api: WordPressApiService = ApiClient.getService(context)
    private val db = AppDatabase.getDatabase(context)
    private val dao = db.dao()

    // In-memory caches to ensure lightning-fast UI responsiveness
    private var cachedPosts: List<ContentItem> = emptyList()
    private var cachedCategories: List<CategoryItem> = emptyList()
    private var cachedTrending: List<ContentItem> = emptyList()
    private val categoryPostsCache = java.util.concurrent.ConcurrentHashMap<Int, List<ContentItem>>()
    private val postByIdCache = java.util.concurrent.ConcurrentHashMap<Int, ContentItem>()
    private val categoryImageCache = java.util.concurrent.ConcurrentHashMap<Int, Pair<String, String>>()

    // Database Flows
    val favorites: Flow<List<FavoriteEntity>> = dao.getAllFavorites()
    val watchlist: Flow<List<WatchlistEntity>> = dao.getAllWatchlist()
    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val notifications: Flow<List<NotificationEntity>> = dao.getAllNotifications()
    val unreadNotificationsCount: Flow<Int> = dao.getUnreadNotificationCount()

    fun isFavorite(id: Int): Flow<Boolean> = dao.isFavorite(id)
    fun isWatchlist(id: Int): Flow<Boolean> = dao.isWatchlist(id)

    suspend fun toggleFavorite(item: ContentItem, isFav: Boolean) = withContext(Dispatchers.IO) {
        if (isFav) {
            dao.deleteFavorite(item.id)
        } else {
            dao.insertFavorite(
                FavoriteEntity(
                    id = item.id,
                    title = item.title,
                    categoryName = item.categoryName,
                    posterUrl = item.posterUrl,
                    videoUrl = item.videoEmbedUrl
                )
            )
        }
    }

    suspend fun toggleWatchlist(item: ContentItem, inList: Boolean) = withContext(Dispatchers.IO) {
        if (inList) {
            dao.deleteWatchlist(item.id)
        } else {
            dao.insertWatchlist(
                WatchlistEntity(
                    id = item.id,
                    title = item.title,
                    categoryName = item.categoryName,
                    posterUrl = item.posterUrl,
                    videoUrl = item.videoEmbedUrl
                )
            )
        }
    }

    suspend fun loginUser(username: String, email: String) = withContext(Dispatchers.IO) {
        dao.saveUserProfile(
            UserProfileEntity(
                id = 1,
                username = username.ifBlank { "Usuario Enpantalla" },
                email = email.ifBlank { "user@enpantallatv.com" },
                avatarUrl = "",
                isLoggedIn = true,
                joinedDate = "2026"
            )
        )
    }

    suspend fun logoutUser() = withContext(Dispatchers.IO) {
        dao.clearUserProfile()
    }

    suspend fun markNotificationAsRead(id: Int) = withContext(Dispatchers.IO) {
        dao.markNotificationRead(id)
    }

    suspend fun markAllNotificationsRead() = withContext(Dispatchers.IO) {
        dao.markAllNotificationsRead()
    }

    fun getInteraction(id: String): Flow<PostInteractionEntity?> = dao.getInteraction(id)

    val recentSearches: Flow<List<RecentSearchEntity>> = dao.getRecentSearches()

    suspend fun saveRecentSearch(query: String) = withContext(Dispatchers.IO) {
        if (query.isNotBlank()) {
            dao.insertRecentSearch(RecentSearchEntity(query = query.trim()))
        }
    }

    suspend fun deleteRecentSearch(query: String) = withContext(Dispatchers.IO) {
        dao.deleteRecentSearch(query)
    }

    suspend fun clearRecentSearches() = withContext(Dispatchers.IO) {
        dao.clearRecentSearches()
    }

    suspend fun toggleLike(id: String) = withContext(Dispatchers.IO) {
        val current = dao.getInteractionSync(id) ?: PostInteractionEntity(
            id = id,
            likesCount = (12..48).random(),
            dislikesCount = (0..2).random(),
            userAction = 0
        )
        val newAction = if (current.userAction == 1) 0 else 1
        val likeDelta = if (current.userAction == 1) -1 else 1
        val dislikeDelta = if (current.userAction == -1) -1 else 0
        dao.saveInteraction(
            current.copy(
                likesCount = (current.likesCount + likeDelta).coerceAtLeast(0),
                dislikesCount = (current.dislikesCount + dislikeDelta).coerceAtLeast(0),
                userAction = newAction
            )
        )
    }

    suspend fun toggleDislike(id: String) = withContext(Dispatchers.IO) {
        val current = dao.getInteractionSync(id) ?: PostInteractionEntity(
            id = id,
            likesCount = (12..48).random(),
            dislikesCount = (0..2).random(),
            userAction = 0
        )
        val newAction = if (current.userAction == -1) 0 else -1
        val dislikeDelta = if (current.userAction == -1) -1 else 1
        val likeDelta = if (current.userAction == 1) -1 else 0
        dao.saveInteraction(
            current.copy(
                likesCount = (current.likesCount + likeDelta).coerceAtLeast(0),
                dislikesCount = (current.dislikesCount + dislikeDelta).coerceAtLeast(0),
                userAction = newAction
            )
        )
    }

    /**
     * Fetch latest posts from WordPress API with _embed
     */
    suspend fun getLatestPosts(forceRefresh: Boolean = false): List<ContentItem> = withContext(Dispatchers.IO) {
        if (!forceRefresh && cachedPosts.isNotEmpty()) {
            return@withContext cachedPosts
        }

        try {
            val raw = api.getPosts(perPage = 20, page = 1, embed = true)
            val mapped = raw.map { mapPostToContent(it) }
            cachedPosts = mapped

            // Seed notifications if empty
            syncNotifications(mapped)

            mapped
        } catch (e: Exception) {
            e.printStackTrace()
            if (cachedPosts.isNotEmpty()) cachedPosts else emptyList()
        }
    }

    /**
     * Trending content:
     * Derived from high-engagement novelas / recent top posts from WordPress
     */
    suspend fun getTrending(forceRefresh: Boolean = false): List<ContentItem> = withContext(Dispatchers.IO) {
        if (!forceRefresh && cachedTrending.isNotEmpty()) {
            return@withContext cachedTrending
        }

        val allPosts = getLatestPosts(forceRefresh)
        // Trending takes diverse series/novelas from recent posts
        val seenCategories = mutableSetOf<Int>()
        val trendingList = mutableListOf<ContentItem>()

        for (post in allPosts) {
            if (seenCategories.add(post.categoryId) || trendingList.size < 6) {
                trendingList.add(post)
            }
            if (trendingList.size >= 8) break
        }

        cachedTrending = trendingList.ifEmpty { allPosts.take(8) }
        cachedTrending
    }

    /**
     * Fetch paginated posts from WordPress API
     */
    suspend fun getPostsByPage(page: Int, perPage: Int = 18): List<ContentItem> = withContext(Dispatchers.IO) {
        try {
            val raw = api.getPosts(perPage = perPage, page = page, embed = true)
            raw.map { mapPostToContent(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Fetch paginated categories from WordPress API
     */
    suspend fun getCategoriesByPage(page: Int, perPage: Int = 100): List<CategoryItem> = withContext(Dispatchers.IO) {
        try {
            val raw = api.getCategories(perPage = perPage, page = page, orderby = "id", order = "desc")
            val posts = getLatestPosts(false)
            val postByCategory = mutableMapOf<Int, ContentItem>()
            posts.forEach { post ->
                if (!postByCategory.containsKey(post.categoryId)) {
                    postByCategory[post.categoryId] = post
                }
            }

            val validCategories = raw.filter { it.name.isNotBlank() }

            // Fetch first post for any category on this page missing an image
            val missingImgCats = validCategories.filter { cat ->
                val cached = categoryImageCache[cat.id]
                val yoastImg = cat.yoastHead?.ogImage?.firstOrNull()?.url
                val catDesc = cat.description ?: ""
                val customSliderImg = extractSliderImageUrl(catDesc) ?: extractImageFromHtml(catDesc)
                customSliderImg == null && postByCategory[cat.id] == null && cached == null && yoastImg.isNullOrBlank()
            }
            if (missingImgCats.isNotEmpty()) {
                kotlinx.coroutines.withTimeoutOrNull(3000) {
                    coroutineScope {
                        missingImgCats.map { cat ->
                            async {
                                try {
                                    val catPosts = api.getPosts(perPage = 1, categoryId = cat.id, embed = true)
                                    if (catPosts.isNotEmpty()) {
                                        val mappedPost = mapPostToContent(catPosts[0])
                                        synchronized(postByCategory) {
                                            postByCategory[cat.id] = mappedPost
                                        }
                                        val img = mappedPost.sliderImageUrl ?: mappedPost.posterUrl
                                        if (!img.isNullOrBlank()) {
                                            categoryImageCache[cat.id] = Pair(mappedPost.posterUrl ?: img, img)
                                        }
                                    }
                                } catch (_: Exception) {}
                            }
                        }.forEach { it.await() }
                    }
                }
            }

            validCategories.map { cat ->
                val firstPost = postByCategory[cat.id]
                val cached = categoryImageCache[cat.id]
                val yoastImg = cat.yoastHead?.ogImage?.firstOrNull()?.url
                val catDesc = cat.description ?: ""
                val customSliderImg = extractSliderImageUrl(catDesc) ?: extractImageFromHtml(catDesc)
                val sampleImg = firstPost?.posterUrl ?: cached?.first ?: yoastImg ?: customSliderImg
                val sliderImg = customSliderImg ?: firstPost?.sliderImageUrl ?: cached?.second ?: sampleImg
                val imdb = firstPost?.imdbRating ?: String.format(java.util.Locale.US, "%.1f", 7.8 + ((cat.id % 18) / 10.0))
                val genre = firstPost?.genre ?: "Novela / Drama"
                val releaseYear = firstPost?.releaseYear ?: "2024"
                val cleanDesc = cleanHtml(catDesc).lines().firstOrNull { !it.contains("slider_image", ignoreCase = true) }
                val shortDesc = firstPost?.shortDescription ?: cleanDesc

                if (sampleImg != null) {
                    categoryImageCache[cat.id] = Pair(sampleImg, sliderImg ?: sampleImg)
                }

                val isMovieCat = cat.name.contains("pelicula", ignoreCase = true) ||
                    cat.name.contains("película", ignoreCase = true) ||
                    cat.name.contains("movie", ignoreCase = true) ||
                    cat.slug.contains("pelicula", ignoreCase = true) ||
                    cat.slug.contains("movie", ignoreCase = true)

                CategoryItem(
                    id = cat.id,
                    name = cleanHtml(cat.name),
                    count = cat.count,
                    slug = cat.slug,
                    description = cleanDesc,
                    sampleImageUrl = sampleImg,
                    imdbRating = imdb,
                    genre = genre,
                    releaseYear = releaseYear,
                    shortDescription = shortDesc,
                    sliderImageUrl = sliderImg,
                    isMovieCategory = isMovieCat
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Fetch categories from WordPress API (Fast parallelized & cached)
     */
    suspend fun getCategories(forceRefresh: Boolean = false): List<CategoryItem> = withContext(Dispatchers.IO) {
        if (!forceRefresh && cachedCategories.isNotEmpty()) {
            return@withContext cachedCategories
        }

        try {
            val raw = api.getCategories(perPage = 100, orderby = "id", order = "desc")
            val posts = getLatestPosts(forceRefresh)
            val postByCategory = mutableMapOf<Int, ContentItem>()
            posts.forEach { post ->
                if (!postByCategory.containsKey(post.categoryId)) {
                    postByCategory[post.categoryId] = post
                }
            }

            val validCategories = raw.filter { it.name.isNotBlank() }

            // Parallel fetch missing images for categories
            val missingImgCats = validCategories.filter { cat ->
                val cached = categoryImageCache[cat.id]
                val yoastImg = cat.yoastHead?.ogImage?.firstOrNull()?.url
                val catDesc = cat.description ?: ""
                val customSliderImg = extractSliderImageUrl(catDesc) ?: extractImageFromHtml(catDesc)
                customSliderImg == null && postByCategory[cat.id] == null && cached == null && yoastImg.isNullOrBlank()
            }
            if (missingImgCats.isNotEmpty()) {
                kotlinx.coroutines.withTimeoutOrNull(3000) {
                    coroutineScope {
                        missingImgCats.map { cat ->
                            async {
                                try {
                                    val catPosts = api.getPosts(perPage = 1, categoryId = cat.id, embed = true)
                                    if (catPosts.isNotEmpty()) {
                                        val mappedPost = mapPostToContent(catPosts[0])
                                        synchronized(postByCategory) {
                                            postByCategory[cat.id] = mappedPost
                                        }
                                        val img = mappedPost.sliderImageUrl ?: mappedPost.posterUrl
                                        if (!img.isNullOrBlank()) {
                                            categoryImageCache[cat.id] = Pair(mappedPost.posterUrl ?: img, img)
                                        }
                                    }
                                } catch (_: Exception) {}
                            }
                        }.forEach { it.await() }
                    }
                }
            }

            val mapped = validCategories.map { cat ->
                val firstPost = postByCategory[cat.id]
                val cached = categoryImageCache[cat.id]
                val yoastImg = cat.yoastHead?.ogImage?.firstOrNull()?.url
                val catDesc = cat.description ?: ""
                val customSliderImg = extractSliderImageUrl(catDesc) ?: extractImageFromHtml(catDesc)
                val sampleImg = firstPost?.posterUrl ?: cached?.first ?: yoastImg ?: customSliderImg
                val sliderImg = customSliderImg ?: firstPost?.sliderImageUrl ?: cached?.second ?: sampleImg
                val imdb = firstPost?.imdbRating ?: String.format(java.util.Locale.US, "%.1f", 7.8 + ((cat.id % 18) / 10.0))
                val genre = firstPost?.genre ?: "Novela / Drama"
                val releaseYear = firstPost?.releaseYear ?: "2024"
                val cleanDesc = cleanHtml(catDesc).lines().firstOrNull { !it.contains("slider_image", ignoreCase = true) }
                val shortDesc = firstPost?.shortDescription ?: cleanDesc

                if (sampleImg != null) {
                    categoryImageCache[cat.id] = Pair(sampleImg, sliderImg ?: sampleImg)
                }

                val isMovieCat = cat.name.contains("pelicula", ignoreCase = true) ||
                    cat.name.contains("película", ignoreCase = true) ||
                    cat.name.contains("movie", ignoreCase = true) ||
                    cat.slug.contains("pelicula", ignoreCase = true) ||
                    cat.slug.contains("movie", ignoreCase = true)

                CategoryItem(
                    id = cat.id,
                    name = cleanHtml(cat.name),
                    count = cat.count,
                    slug = cat.slug,
                    description = cleanDesc,
                    sampleImageUrl = sampleImg,
                    imdbRating = imdb,
                    genre = genre,
                    releaseYear = releaseYear,
                    shortDescription = shortDesc,
                    sliderImageUrl = sliderImg,
                    isMovieCategory = isMovieCat
                )
            }

            cachedCategories = mapped
            mapped
        } catch (e: Exception) {
            e.printStackTrace()
            if (cachedCategories.isNotEmpty()) cachedCategories else emptyList()
        }
    }

    /**
     * Fetch posts for a specific category (Cached for instant switching)
     */
    suspend fun getCategoryPosts(categoryId: Int, page: Int = 1): List<ContentItem> = withContext(Dispatchers.IO) {
        if (page == 1 && categoryPostsCache.containsKey(categoryId)) {
            val cached = categoryPostsCache[categoryId]
            if (!cached.isNullOrEmpty()) return@withContext cached
        }

        try {
            val raw = api.getPosts(perPage = 20, page = page, embed = true, categoryId = categoryId)
            val mapped = raw.map { mapPostToContent(it) }
            if (page == 1 && mapped.isNotEmpty()) {
                categoryPostsCache[categoryId] = mapped
            }
            mapped
        } catch (e: Exception) {
            e.printStackTrace()
            categoryPostsCache[categoryId] ?: emptyList()
        }
    }

    /**
     * Search posts on WordPress
     */
    suspend fun search(query: String): List<ContentItem> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        try {
            val raw = api.getPosts(perPage = 20, search = query, embed = true)
            raw.map { mapPostToContent(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Fast synchronously derived trending
     */
    fun deriveTrending(allPosts: List<ContentItem>): List<ContentItem> {
        val explicitTrending = allPosts.filter { it.isTrending && !it.isUpcoming }
        if (explicitTrending.isNotEmpty()) {
            return explicitTrending
        }
        val seenCategories = mutableSetOf<Int>()
        val trendingList = mutableListOf<ContentItem>()
        for (post in allPosts) {
            if (!post.isUpcoming && (seenCategories.add(post.categoryId) || trendingList.size < 6)) {
                trendingList.add(post)
            }
            if (trendingList.size >= 10) break
        }
        val result = trendingList.ifEmpty { allPosts.filter { !it.isUpcoming }.take(10) }
        return result
    }

    /**
     * Get single post by ID (With instant cache lookup)
     */
    suspend fun getPostById(id: Int): ContentItem? = withContext(Dispatchers.IO) {
        // Check cache first
        val cached = cachedPosts.find { it.id == id } ?: postByIdCache[id]
        if (cached != null) return@withContext cached

        try {
            val raw = api.getPostById(id, embed = true)
            mapPostToContent(raw)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Synchronize notifications based on latest releases
     */
    private suspend fun syncNotifications(posts: List<ContentItem>) {
        if (posts.isEmpty()) return
        val notifs = posts.take(5).map { post ->
            NotificationEntity(
                id = post.id,
                title = "Nuevo Episodio Disponible",
                message = "${post.title} ya está listo para ver en EnpantallaTV.",
                timestamp = System.currentTimeMillis(),
                isRead = false,
                postId = post.id
            )
        }
        dao.insertNotifications(notifs)
    }

    /**
     * Map WordPress Raw Post Response to clean ContentItem
     */
    private fun mapPostToContent(post: WpPostResponse): ContentItem {
        val rawTitle = post.title?.rendered ?: "Sin Título"
        val cleanTitle = cleanHtml(rawTitle)

        // 1. Featured Media / Image Extraction
        var posterUrl: String? = null
        val mediaList = post.embedded?.wpFeaturedMedia
        if (!mediaList.isNullOrEmpty()) {
            posterUrl = mediaList[0].sourceUrl
        }
        if (posterUrl.isNullOrBlank()) {
            val ogImages = post.yoastHead?.ogImage
            if (!ogImages.isNullOrEmpty()) {
                posterUrl = ogImages[0].url
            }
        }
        if (posterUrl.isNullOrBlank()) {
            posterUrl = extractImageFromHtml(post.content?.rendered ?: "")
                ?: extractImageFromHtml(post.excerpt?.rendered ?: "")
        }
        // Normalize http to https
        if (posterUrl != null && posterUrl.startsWith("http://")) {
            posterUrl = posterUrl.replaceFirst("http://", "https://")
        }

        // 2. Category Extraction
        var categoryId = 0
        var categoryName = "Series y Novelas"
        val termGroups = post.embedded?.wpTerm
        if (!termGroups.isNullOrEmpty()) {
            val categoryTerms = termGroups[0]
            if (categoryTerms.isNotEmpty()) {
                val term = categoryTerms[0]
                categoryId = term.id ?: 0
                categoryName = cleanHtml(term.name ?: "Series y Novelas")
            }
        }
        if (categoryId == 0 && !post.categories.isNullOrEmpty()) {
            categoryId = post.categories[0]
        }
        if (posterUrl.isNullOrBlank() && categoryId > 0) {
            posterUrl = categoryImageCache[categoryId]?.first
        }

        // 3. Video Embed URL Extraction
        val contentHtml = post.content?.rendered ?: ""
        val videoEmbedUrl = extractVideoUrl(contentHtml)

        // 4. Episode badge detection
        val episodeBadge = extractEpisodeBadge(cleanTitle)

        // 5. Year extraction
        val year = extractYear(post.date)

        // 6. Clean Description
        var description = post.yoastHead?.description
        if (description.isNullOrBlank()) {
            val rawExcerpt = post.excerpt?.rendered ?: ""
            description = cleanHtml(rawExcerpt)
        }
        if (description.isBlank()) {
            description = "Disfruta de ${cleanTitle} en streaming de alta calidad en EnpantallaTV."
        }

        // 7. Custom Fields (IMDb rating, genre, release_year, short_description)
        val contentHtmlFull = post.content?.rendered ?: ""
        val excerptHtmlFull = post.excerpt?.rendered ?: ""
        val combinedText = "$contentHtmlFull $excerptHtmlFull"

        // Custom field: slider_image URL added by user
        val sliderImage = extractSliderImageUrl(combinedText) ?: posterUrl

        val imdbRating = extractRegex(combinedText, "(?:imdb_rating|imdb)[^0-9]*([0-9]+(?:\\.[0-9]+)?)")
            ?: String.format(java.util.Locale.US, "%.1f", 7.8 + ((post.id % 18) / 10.0))
        val genre = extractRegex(combinedText, "genre\\s*[:=]\\s*([^<\\n\\r]+)")
            ?: if (categoryName.contains("Novela", ignoreCase = true)) "Novela / Romance" else "Drama / Serie"
        val releaseYear = extractRegex(combinedText, "release_year\\s*[:=]\\s*([0-9]{4})")
            ?: year
        val shortDesc = extractRegex(combinedText, "short_description\\s*[:=]\\s*([^<\\n\\r]+)")
            ?: description

        // 8. Upcoming release detection & Date Badge extraction
        val isUpcomingCheck = cleanTitle.contains("upcoming", ignoreCase = true) ||
            cleanTitle.contains("proximamente", ignoreCase = true) ||
            cleanTitle.contains("próximamente", ignoreCase = true) ||
            combinedText.contains("upcoming", ignoreCase = true) ||
            combinedText.contains("proximamente", ignoreCase = true) ||
            combinedText.contains("próximamente", ignoreCase = true) ||
            (post.slug?.contains("upcoming", ignoreCase = true) == true)

        var upcomingDate: String? = null
        var displayTitle = cleanTitle
        if (isUpcomingCheck) {
            // Explicit upcoming_date / fecha_estreno / upcoming
            upcomingDate = extractRegex(combinedText, "(?:upcoming_date|upcoming|proximamente|próximamente|fecha_estreno|estreno)\\s*[:=]\\s*([^<\\n\\r]+)")
            if (upcomingDate.isNullOrBlank()) {
                val titleMatcher = Pattern.compile("(?:upcoming|proximamente|próximamente)[^a-zA-Z0-9]*([0-9]{1,2}(?:\\s*(?:de\\s+)?[a-zA-Z]+)?|[0-9]{1,2}/[0-9]{1,2}/?[0-9]{0,4}|[0-9]{4}-[0-9]{2}-[0-9]{2})", Pattern.CASE_INSENSITIVE).matcher(cleanTitle)
                if (titleMatcher.find()) {
                    upcomingDate = titleMatcher.group(1)?.trim()
                }
            }
            displayTitle = displayTitle.replace(Regex("^\\[?(?:upcoming|proximamente|próximamente)[^\\]\\-–:]*[\\]\\-–:]?\\s*", RegexOption.IGNORE_CASE), "").trim()
            if (displayTitle.isBlank()) displayTitle = cleanTitle

            if (upcomingDate.isNullOrBlank()) {
                upcomingDate = "Próximamente"
            }
        }

        // 9. Trending detection
        val isTrendingCheck = combinedText.contains("trending", ignoreCase = true) ||
            combinedText.contains("tendencia", ignoreCase = true) ||
            cleanTitle.contains("trending", ignoreCase = true) ||
            cleanTitle.contains("tendencia", ignoreCase = true) ||
            (post.slug?.contains("trending", ignoreCase = true) == true)

        // 10. Movie / Película detection
        val isMovieCheck = combinedText.contains("movie", ignoreCase = true) ||
            combinedText.contains("pelicula", ignoreCase = true) ||
            combinedText.contains("película", ignoreCase = true) ||
            combinedText.contains("peliculas", ignoreCase = true) ||
            combinedText.contains("películas", ignoreCase = true) ||
            categoryName.contains("pelicula", ignoreCase = true) ||
            categoryName.contains("película", ignoreCase = true) ||
            categoryName.contains("movie", ignoreCase = true) ||
            cleanTitle.contains("pelicula", ignoreCase = true) ||
            cleanTitle.contains("película", ignoreCase = true) ||
            (post.slug?.contains("pelicula", ignoreCase = true) == true) ||
            (post.slug?.contains("movie", ignoreCase = true) == true)

        val item = ContentItem(
            id = post.id,
            title = displayTitle,
            rawTitle = rawTitle,
            categoryId = categoryId,
            categoryName = categoryName,
            posterUrl = sliderImage ?: posterUrl,
            backdropUrl = sliderImage ?: posterUrl,
            videoEmbedUrl = videoEmbedUrl ?: "https://enpantallatv.me/?p=${post.id}",
            episodeBadge = if (isMovieCheck) "PELÍCULA" else episodeBadge,
            year = year,
            quality = "HD",
            rating = imdbRating,
            description = description,
            rawDate = post.date ?: "",
            imdbRating = imdbRating,
            genre = genre,
            releaseYear = releaseYear,
            shortDescription = shortDesc,
            sliderImageUrl = sliderImage,
            isUpcoming = isUpcomingCheck,
            upcomingDateBadge = upcomingDate?.uppercase(),
            isTrending = isTrendingCheck,
            isMovie = isMovieCheck
        )
        postByIdCache[post.id] = item
        if (categoryId > 0) {
            val img = item.sliderImageUrl ?: item.posterUrl
            if (!img.isNullOrBlank()) {
                categoryImageCache[categoryId] = Pair(item.posterUrl ?: img, img)
            }
        }
        return item
    }

    private fun extractImageFromHtml(html: String): String? {
        if (html.isBlank()) return null
        val patterns = listOf(
            "<img[^>]+(?:src|data-src|data-lazy-src)=[\"']([^\"']+)[\"']",
            "(https?://[^\"'<>\\s]+\\.(?:jpg|jpeg|png|webp))"
        )
        for (pat in patterns) {
            val p = Pattern.compile(pat, Pattern.CASE_INSENSITIVE)
            val m = p.matcher(html)
            if (m.find()) {
                var url = m.group(1)?.trim() ?: continue
                if (url.startsWith("//")) url = "https:$url"
                else if (url.startsWith("http://")) url = url.replaceFirst("http://", "https://")
                return url
            }
        }
        return null
    }

    private fun extractSliderImageUrl(content: String): String? {
        if (content.isBlank()) return null
        val patterns = listOf(
            // slider_image: https://... or sldier_image: https://...
            "(?:slider_image|sldier_image|slider_img|sldier_img)\\s*[:=]\\s*[\"']?([^\"'<>\\s\\n\\r]+)",
            // [slider_image]https://...[/slider_image] or [sldier_image]...
            "\\[(?:slider_image|sldier_image)\\]([^\\[]+)\\[/(?:slider_image|sldier_image)\\]",
            "<(?:slider_image|sldier_image)>([^<]+)</(?:slider_image|sldier_image)>",
            // "slider_image": "https://..."
            "\"(?:slider_image|sldier_image|slider_img|sldier_img)\"\\s*:\\s*\"([^\"]+)\"",
            // Any URL directly following slider_image or sldier_image
            "(?:slider_image|sldier_image|slider_img|sldier_img)[^a-zA-Z0-9_]*[\"']?((?:https?:)?//[^\"'<>\\s\\n\\r]+)"
        )
        for (pat in patterns) {
            val p = Pattern.compile(pat, Pattern.CASE_INSENSITIVE)
            val m = p.matcher(content)
            if (m.find()) {
                var url = m.group(1)?.trim()
                if (!url.isNullOrBlank()) {
                    if (url.startsWith("//")) url = "https:$url"
                    else if (url.startsWith("http://")) url = url.replaceFirst("http://", "https://")
                    return url
                }
            }
        }
        return null
    }

    private fun extractRegex(content: String, regex: String): String? {
        if (content.isBlank()) return null
        val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(content)
        if (matcher.find()) {
            return matcher.group(1)?.trim()
        }
        return null
    }

    private fun extractVideoUrl(contentHtml: String): String? {
        if (contentHtml.isBlank()) return null
        // 1. Match iframe src or video src
        val iframePattern = Pattern.compile("<(?:iframe|video)[^>]+src=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE)
        val m1 = iframePattern.matcher(contentHtml)
        if (m1.find()) {
            var url = m1.group(1)?.trim() ?: return null
            if (url.startsWith("//")) url = "https:$url"
            return url
        }

        // 2. Match data-src or data-video
        val dataSrcPattern = Pattern.compile("data-(?:src|video|url)=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE)
        val m2 = dataSrcPattern.matcher(contentHtml)
        if (m2.find()) {
            var url = m2.group(1)?.trim() ?: return null
            if (url.startsWith("//")) url = "https:$url"
            return url
        }

        // 3. Match ok.ru videoembed or video URL
        val okRuPattern = Pattern.compile("(https?://(?:www\\.)?ok\\.ru/(?:videoembed|video)/[0-9]+)", Pattern.CASE_INSENSITIVE)
        val m3 = okRuPattern.matcher(contentHtml)
        if (m3.find()) {
            var url = m3.group(1)?.trim() ?: return null
            if (url.contains("/video/") && !url.contains("/videoembed/")) {
                url = url.replace("/video/", "/videoembed/")
            }
            return url
        }

        // 4. Match common streaming embed host patterns
        val hostPattern = Pattern.compile("(https?://[^\"'<>\\s]+(?:embed|player|video)[^\"'<>\\s]*)", Pattern.CASE_INSENSITIVE)
        val m4 = hostPattern.matcher(contentHtml)
        if (m4.find()) {
            return m4.group(1)?.trim()
        }

        return null
    }

    private fun extractEpisodeBadge(title: String): String? {
        // 1. Detect "Capítulo 47", "Capitulo 1", "Cap 12", "Episodio 05", "Ep 3", "Cap-1", "Ep.1"
        val capPattern = Pattern.compile("(?i)(?:cap[ií]tulo|cap\\.?|episodio|ep\\.?|parte|temporada\\s*\\d+\\s*ep\\.?)\\s*(\\d+)")
        val m1 = capPattern.matcher(title)
        if (m1.find()) {
            val num = m1.group(1)?.toIntOrNull() ?: m1.group(1)
            return "Cap. $num"
        }

        // 2. Trailing episode number: e.g. "Serie Name - 01" or "Serie Name 1" or "#1"
        val trailingPattern = Pattern.compile("(?i)(?:[-–—:|])?\\s*(?:#|no\\.?\\s*)?(\\d+)\\s*$")
        val m2 = trailingPattern.matcher(title.trim())
        if (m2.find()) {
            val num = m2.group(1)?.toIntOrNull() ?: m2.group(1)
            return "Cap. $num"
        }

        // 3. Bracketed number: "[01]" or "(01)"
        val bracketPattern = Pattern.compile("[\\[\\(](\\d+)[\\]\\)]")
        val m3 = bracketPattern.matcher(title)
        if (m3.find()) {
            val num = m3.group(1)?.toIntOrNull() ?: m3.group(1)
            return "Cap. $num"
        }

        return null
    }

    private fun extractYear(dateStr: String?): String? {
        if (dateStr.isNullOrBlank()) return "2026"
        return if (dateStr.length >= 4) {
            dateStr.substring(0, 4)
        } else "2026"
    }

    private fun cleanHtml(html: String): String {
        if (html.isBlank()) return ""
        val decoded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(html).toString()
        }
        return decoded
            .replace("\n", " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}
