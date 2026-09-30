package com.example

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.google.android.gms.ads.MobileAds
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class EnpantallaApp : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        try {
            MobileAds.initialize(this) { /* MobileAds Initialized */ }
        } catch (_: Exception) {}
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25) // Higher memory cache
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.05)
                    .build()
            }
            .okHttpClient {
                OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build()
            }
            .crossfade(true)
            .respectCacheHeaders(false) // Cache more aggressively
            .build()
    }
}
