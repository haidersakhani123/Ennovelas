```kotlin
package com.example

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.google.android.gms.ads.MobileAds
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class EnpantallaApp : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()

        // Google Mobile Ads
        try {
            MobileAds.initialize(this) {
                // MobileAds Initialized
            }
        } catch (_: Exception) {
        }

        // OneSignal Push Notifications
        try {
            OneSignal.Debug.logLevel = LogLevel.VERBOSE

            OneSignal.initWithContext(
                this,
                "4fc68d68-b0f9-4849-91e8-d740ca3ca566"
            )

            OneSignal.Notifications.requestPermission(false)
        } catch (_: Exception) {
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
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
            .respectCacheHeaders(false)
            .build()
    }
}
```
