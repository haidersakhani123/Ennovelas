package com.example.data.api

import android.content.Context
import com.example.data.model.WpCategoryResponse
import com.example.data.model.WpPostResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.File
import java.util.concurrent.TimeUnit

interface WordPressApiService {

    @GET("posts")
    suspend fun getPosts(
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1,
        @Query("_embed") embed: Boolean = true,
        @Query("categories") categoryId: Int? = null,
        @Query("search") search: String? = null,
        @Query("orderby") orderby: String = "date",
        @Query("order") order: String = "desc"
    ): List<WpPostResponse>

    @GET("posts/{id}")
    suspend fun getPostById(
        @Path("id") id: Int,
        @Query("_embed") embed: Boolean = true
    ): WpPostResponse

    @GET("categories")
    suspend fun getCategories(
        @Query("per_page") perPage: Int = 50,
        @Query("page") page: Int = 1,
        @Query("orderby") orderby: String = "count",
        @Query("order") order: String = "desc",
        @Query("hide_empty") hideEmpty: Boolean = false
    ): List<WpCategoryResponse>
}

object ApiClient {
    private const val BASE_URL = "https://enpantallatv.me/wp-json/wp/v2/"
    private var apiServiceInstance: WordPressApiService? = null

    fun getService(context: Context? = null): WordPressApiService {
        if (apiServiceInstance == null) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val clientBuilder = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .header("User-Agent", "EnpantallaTV/1.0 (Android; Mobile)")
                        .header("Accept", "application/json")
                        .build()
                    chain.proceed(request)
                }
                .addInterceptor(logging)

            if (context != null) {
                val cacheDir = File(context.cacheDir, "http_cache")
                clientBuilder.cache(Cache(cacheDir, 20L * 1024 * 1024))
            }

            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(clientBuilder.build())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            apiServiceInstance = retrofit.create(WordPressApiService::class.java)
        }
        return apiServiceInstance!!
    }
}
