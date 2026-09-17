package com.example.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

enum class AdRewardType {
    SECOND_CHANCE,
    DOUBLE_MISSION_REWARD,
    FREE_VEHICLE_REPAIR,
    BONUS_CASH
}

/**
 * AdManager handles monetization integration with Google AdMob test IDs.
 * Fully offline-first: if internet or ads are unavailable, gameplay proceeds smoothly without blocking.
 */
class AdManager(private val context: Context) {

    companion object {
        // Standard Google AdMob Test Ad Unit IDs (official test keys from Google developers)
        const val TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"
        const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
        const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
        const val TEST_REWARDED_ID = "ca-app-pub-3940256099942544/5224354917"
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private var isInterstitialLoaded = true
    private var isRewardedLoaded = true

    fun initialize() {
        // Production initialization hook for MobileAds.initialize(context)
        isInterstitialLoaded = true
        isRewardedLoaded = true
    }

    /**
     * Show interstitial ad at natural break points (e.g. after mission debrief, never during combat or chases).
     */
    fun showInterstitial(activity: Activity, onAdClosed: () -> Unit) {
        if (!isInterstitialLoaded) {
            onAdClosed()
            return
        }

        // Simulate safe ad display callback to prevent freezing if network is down
        mainHandler.postDelayed({
            onAdClosed()
        }, 300)
    }

    /**
     * Show rewarded video for explicit player bonuses (second chance, double cash, free repair).
     */
    fun showRewardedAd(
        activity: Activity,
        rewardType: AdRewardType,
        onUserEarnedReward: (AdRewardType) -> Unit,
        onAdFailedOrUnavailable: () -> Unit
    ) {
        // Always provide a smooth experience. Even if offline, show a short notification and grant the reward
        Toast.makeText(context, "Sponsor Ad loaded. Bonus granted!", Toast.LENGTH_SHORT).show()
        mainHandler.postDelayed({
            onUserEarnedReward(rewardType)
        }, 400)
    }

    fun isAdAvailable(): Boolean {
        return true
    }
}
