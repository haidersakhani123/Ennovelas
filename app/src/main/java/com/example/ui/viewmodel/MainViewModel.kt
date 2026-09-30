package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.FavoriteEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.PostInteractionEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.WatchlistEntity
import com.example.data.model.CategoryItem
import com.example.data.model.ContentItem
import com.example.data.repository.ContentRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ContentRepository(application.applicationContext)

    // Splash State
    private val _isSplashFinished = MutableStateFlow(false)
    val isSplashFinished: StateFlow<Boolean> = _isSplashFinished.asStateFlow()

    // Home Screen Data
    private val _isLoadingHome = MutableStateFlow(true)
    val isLoadingHome: StateFlow<Boolean> = _isLoadingHome.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _heroItems = MutableStateFlow<List<ContentItem>>(emptyList())
    val heroItems: StateFlow<List<ContentItem>> = _heroItems.asStateFlow()

    private val _trendingItems = MutableStateFlow<List<ContentItem>>(emptyList())
    val trendingItems: StateFlow<List<ContentItem>> = _trendingItems.asStateFlow()

    private val _latestEpisodes = MutableStateFlow<List<ContentItem>>(emptyList())
    val latestEpisodes: StateFlow<List<ContentItem>> = _latestEpisodes.asStateFlow()

    private val _upcomingItems = MutableStateFlow<List<ContentItem>>(emptyList())
    val upcomingItems: StateFlow<List<ContentItem>> = _upcomingItems.asStateFlow()

    private val _movieItems = MutableStateFlow<List<ContentItem>>(emptyList())
    val movieItems: StateFlow<List<ContentItem>> = _movieItems.asStateFlow()

    private val _categories = MutableStateFlow<List<CategoryItem>>(emptyList())
    val categories: StateFlow<List<CategoryItem>> = _categories.asStateFlow()

    // Category Screen
    private val _selectedCategory = MutableStateFlow<CategoryItem?>(null)
    val selectedCategory: StateFlow<CategoryItem?> = _selectedCategory.asStateFlow()

    private val _categoryPosts = MutableStateFlow<List<ContentItem>>(emptyList())
    val categoryPosts: StateFlow<List<ContentItem>> = _categoryPosts.asStateFlow()

    private val _isLoadingCategory = MutableStateFlow(false)
    val isLoadingCategory: StateFlow<Boolean> = _isLoadingCategory.asStateFlow()

    // Search Screen
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<ContentItem>>(emptyList())
    val searchResults: StateFlow<List<ContentItem>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()
    private var searchJob: Job? = null

    // Detail & Watch Screen
    private val _currentDetailItem = MutableStateFlow<ContentItem?>(null)
    val currentDetailItem: StateFlow<ContentItem?> = _currentDetailItem.asStateFlow()

    private val _relatedEpisodes = MutableStateFlow<List<ContentItem>>(emptyList())
    val relatedEpisodes: StateFlow<List<ContentItem>> = _relatedEpisodes.asStateFlow()

    // Pagination for All Episodes Screen
    private val _allEpisodes = MutableStateFlow<List<ContentItem>>(emptyList())
    val allEpisodes: StateFlow<List<ContentItem>> = _allEpisodes.asStateFlow()

    private val _allEpisodesPage = MutableStateFlow(1)
    val allEpisodesPage: StateFlow<Int> = _allEpisodesPage.asStateFlow()

    private val _isLoadingAllEpisodes = MutableStateFlow(false)
    val isLoadingAllEpisodes: StateFlow<Boolean> = _isLoadingAllEpisodes.asStateFlow()

    // Pagination for All Categories Screen
    private val _paginatedCategories = MutableStateFlow<List<CategoryItem>>(emptyList())
    val paginatedCategories: StateFlow<List<CategoryItem>> = _paginatedCategories.asStateFlow()

    private val _allCategoriesPage = MutableStateFlow(1)
    val allCategoriesPage: StateFlow<Int> = _allCategoriesPage.asStateFlow()

    private val _isLoadingAllCategories = MutableStateFlow(false)
    val isLoadingAllCategories: StateFlow<Boolean> = _isLoadingAllCategories.asStateFlow()

    // HD Unlock State (Session-based)
    private val _isHdUnlocked = MutableStateFlow(false)
    val isHdUnlocked: StateFlow<Boolean> = _isHdUnlocked.asStateFlow()

    fun unlockHd() {
        _isHdUnlocked.value = true
    }

    // Database flows
    val favorites: StateFlow<List<FavoriteEntity>> = repository.favorites.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val watchlist: StateFlow<List<WatchlistEntity>> = repository.watchlist.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentSearches: StateFlow<List<com.example.data.local.RecentSearchEntity>> = repository.recentSearches.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val notifications: StateFlow<List<NotificationEntity>> = repository.notifications.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val unreadNotifCount: StateFlow<Int> = repository.unreadNotificationsCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    init {
        loadHomeData()
    }

    fun finishSplash() {
        _isSplashFinished.value = true
    }

    fun loadHomeData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (forceRefresh) {
                _isRefreshing.value = true
            } else if (_heroItems.value.isEmpty()) {
                _isLoadingHome.value = true
            }
            _errorMessage.value = null

            try {
                // Fetch posts and categories in parallel for speed
                val postsDeferred = async { repository.getLatestPosts(forceRefresh) }
                val catsDeferred = async { repository.getCategories(forceRefresh) }

                val posts = postsDeferred.await()
                if (posts.isNotEmpty()) {
                    val movies = posts.filter { it.isMovie }
                    val upcoming = posts.filter { it.isUpcoming && !it.isMovie }
                    val regularPosts = posts.filter { !it.isUpcoming && !it.isMovie }

                    _movieItems.value = movies
                    _upcomingItems.value = upcoming
                    _heroItems.value = regularPosts.take(5)
                    _latestEpisodes.value = regularPosts
                    _trendingItems.value = repository.deriveTrending(regularPosts)
                    // Release home loader as soon as primary content is ready
                    _isLoadingHome.value = false
                }

                val cats = catsDeferred.await()
                _categories.value = cats.filter { !it.isMovieCategory }
            } catch (e: Exception) {
                e.printStackTrace()
                if (_heroItems.value.isEmpty()) {
                    _errorMessage.value = "Unable to load content right now."
                }
            } finally {
                _isLoadingHome.value = false
                _isRefreshing.value = false
            }
        }
    }

    fun selectCategory(category: CategoryItem) {
        _selectedCategory.value = category
        viewModelScope.launch {
            _isLoadingCategory.value = true
            try {
                val posts = repository.getCategoryPosts(category.id)
                _categoryPosts.value = posts
            } catch (e: Exception) {
                _categoryPosts.value = emptyList()
            } finally {
                _isLoadingCategory.value = false
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()

        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }

        // Live instant search with 250ms debounce
        searchJob = viewModelScope.launch {
            delay(250)
            _isSearching.value = true
            try {
                val results = repository.search(query.trim())
                _searchResults.value = results
                if (query.trim().length >= 2) {
                    repository.saveRecentSearch(query.trim())
                }
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun executeSearch(query: String) {
        _searchQuery.value = query
        onSearchQueryChange(query)
    }

    fun deleteRecentSearch(query: String) {
        viewModelScope.launch {
            repository.deleteRecentSearch(query)
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            repository.clearRecentSearches()
        }
    }

    fun selectDetail(item: ContentItem) {
        _currentDetailItem.value = item
        // Pre-populate related episodes immediately to prevent button jumps
        if (item.categoryId > 0) {
            val quickRelated = _latestEpisodes.value.filter { it.categoryId == item.categoryId }
            if (quickRelated.isNotEmpty() && _relatedEpisodes.value.none { it.categoryId == item.categoryId }) {
                _relatedEpisodes.value = quickRelated
            }
        }

        viewModelScope.launch {
            // If item has placeholder or no video url, fetch single post to get embed
            if (item.videoEmbedUrl.isNullOrBlank() || item.videoEmbedUrl.contains("?p=")) {
                val fullPost = repository.getPostById(item.id)
                if (fullPost != null && !fullPost.videoEmbedUrl.isNullOrBlank() && !fullPost.videoEmbedUrl.contains("?p=")) {
                    _currentDetailItem.value = fullPost
                }
            }

            // Load all episodes of this category for the Cap buttons (with caching)
            if (item.categoryId > 0) {
                val catPosts = repository.getCategoryPosts(item.categoryId)
                if (catPosts.isNotEmpty()) {
                    _relatedEpisodes.value = catPosts
                }
            }
        }
    }

    fun selectDetailById(id: Int) {
        viewModelScope.launch {
            val item = repository.getPostById(id)
            if (item != null) {
                selectDetail(item)
            }
        }
    }

    fun toggleFavorite(item: ContentItem) {
        viewModelScope.launch {
            val isFav = favorites.value.any { it.id == item.id }
            repository.toggleFavorite(item, isFav)
        }
    }

    fun toggleWatchlist(item: ContentItem) {
        viewModelScope.launch {
            val inList = watchlist.value.any { it.id == item.id }
            repository.toggleWatchlist(item, inList)
        }
    }

    fun isFavorite(id: Int): Boolean {
        return favorites.value.any { it.id == id }
    }

    fun isWatchlist(id: Int): Boolean {
        return watchlist.value.any { it.id == id }
    }

    fun loginUser(username: String, email: String) {
        viewModelScope.launch {
            repository.loginUser(username, email)
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            repository.logoutUser()
        }
    }

    fun markNotificationRead(id: Int) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    fun loadAllEpisodes(page: Int) {
        val targetPage = if (page < 1) 1 else page
        _allEpisodesPage.value = targetPage
        viewModelScope.launch {
            _isLoadingAllEpisodes.value = true
            try {
                val posts = repository.getPostsByPage(targetPage, perPage = 24).filter { !it.isUpcoming && !it.isMovie }
                _allEpisodes.value = posts
            } catch (e: Exception) {
                _allEpisodes.value = emptyList()
            } finally {
                _isLoadingAllEpisodes.value = false
            }
        }
    }

    fun loadAllCategories(page: Int) {
        val targetPage = if (page < 1) 1 else page
        _allCategoriesPage.value = targetPage
        viewModelScope.launch {
            _isLoadingAllCategories.value = true
            try {
                val cats = repository.getCategoriesByPage(targetPage, perPage = 30).filter { !it.isMovieCategory }
                _paginatedCategories.value = cats
            } catch (e: Exception) {
                _paginatedCategories.value = emptyList()
            } finally {
                _isLoadingAllCategories.value = false
            }
        }
    }

    fun getInteraction(id: String): kotlinx.coroutines.flow.Flow<PostInteractionEntity?> {
        return repository.getInteraction(id)
    }

    fun toggleLike(id: String) {
        viewModelScope.launch {
            repository.toggleLike(id)
        }
    }

    fun toggleDislike(id: String) {
        viewModelScope.launch {
            repository.toggleDislike(id)
        }
    }
}
