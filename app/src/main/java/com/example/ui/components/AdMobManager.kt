package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

/**
 * AdMobManager:
 * Centralized manager for Google Mobile Ads SDK (AdMob).
 * To switch to production ads later, simply replace the 3 Unit IDs below with your real AdMob unit IDs.
 */
object AdMobManager {
    private const val TAG = "AdMobManager"

    // ==========================================
    // ⚙️ GOOGLE ADMOB TESTING & PRODUCTION IDS
    // ==========================================
    var BANNER_AD_UNIT_ID: String = "ca-app-pub-3940256099942544/6300978111"
    var INTERSTITIAL_AD_UNIT_ID: String = "ca-app-pub-3940256099942544/1033173712"
    var REWARDED_AD_UNIT_ID: String = "ca-app-pub-3940256099942544/5224354917"

    // In-memory preloaded ad instances
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    private var isInterstitialLoading = false
    private var isRewardedLoading = false

    /**
     * Preload Interstitial Ad
     */
    fun loadInterstitial(context: Context) {
        if (interstitialAd != null || isInterstitialLoading) return
        isInterstitialLoading = true

        try {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                INTERSTITIAL_AD_UNIT_ID,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        isInterstitialLoading = false
                        Log.d(TAG, "AdMob Interstitial Ad Loaded successfully")
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        interstitialAd = null
                        isInterstitialLoading = false
                        Log.w(TAG, "AdMob Interstitial Ad Failed: ${loadAdError.message}")
                    }
                }
            )
        } catch (e: Exception) {
            isInterstitialLoading = false
            Log.e(TAG, "Error loading interstitial ad", e)
        }
    }

    /**
     * Show Interstitial Ad. If real ad is loaded, shows it.
     * Calls onAdDismissed when ad finishes or fails.
     */
    fun showInterstitial(activity: Activity?, onAdDismissed: () -> Unit) {
        val currentAd = interstitialAd
        if (currentAd != null && activity != null) {
            currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitial(activity)
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    loadInterstitial(activity)
                    onAdDismissed()
                }
            }
            currentAd.show(activity)
        } else {
            // Fallback
            activity?.let { loadInterstitial(it) }
            onAdDismissed()
        }
    }

    /**
     * Preload Rewarded Ad
     */
    fun loadRewarded(context: Context) {
        if (rewardedAd != null || isRewardedLoading) return
        isRewardedLoading = true

        try {
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                context,
                REWARDED_AD_UNIT_ID,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                        isRewardedLoading = false
                        Log.d(TAG, "AdMob Rewarded Ad Loaded successfully")
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        rewardedAd = null
                        isRewardedLoading = false
                        Log.w(TAG, "AdMob Rewarded Ad Failed: ${loadAdError.message}")
                    }
                }
            )
        } catch (e: Exception) {
            isRewardedLoading = false
            Log.e(TAG, "Error loading rewarded ad", e)
        }
    }

    /**
     * Show Rewarded Ad.
     */
    fun showRewarded(
        activity: Activity?,
        onRewardEarned: () -> Unit,
        onAdClosed: () -> Unit
    ) {
        val currentAd = rewardedAd
        if (currentAd != null && activity != null) {
            var rewardGranted = false
            currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    loadRewarded(activity)
                    if (rewardGranted) {
                        onRewardEarned()
                    } else {
                        onAdClosed()
                    }
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    loadRewarded(activity)
                    onAdClosed()
                }
            }
            currentAd.show(activity) { _ ->
                rewardGranted = true
            }
        } else {
            activity?.let { loadRewarded(it) }
            onAdClosed()
        }
    }

    fun hasInterstitial(): Boolean = interstitialAd != null
    fun hasRewarded(): Boolean = rewardedAd != null
}
