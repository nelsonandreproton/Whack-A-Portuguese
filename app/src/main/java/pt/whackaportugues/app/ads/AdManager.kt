package pt.whackaportugues.app.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * Manages AdMob ads in a non-intrusive way:
 * - Banners only on menu and leaderboard screens
 * - Interstitial only between rounds (after result screen, before character select)
 * - NEVER during gameplay
 *
 * TODO (production): Replace TEST_* IDs with real AdMob unit IDs.
 */
object AdManager {

    // Test IDs — replace with real ones from AdMob console for production
    const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false

    fun loadBanner(adView: AdView) {
        adView.loadAd(AdRequest.Builder().build())
    }

    fun preloadInterstitial(context: Context) {
        if (isLoading || interstitialAd != null) return
        isLoading = true
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                }
            }
        )
    }

    /**
     * Shows an interstitial if available, then calls [onClosed].
     * If no ad is available, [onClosed] is called immediately.
     */
    fun showInterstitial(activity: Activity, onClosed: () -> Unit) {
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    onClosed()
                    // Preload next one
                    preloadInterstitial(activity)
                }
            }
            ad.show(activity)
        } else {
            onClosed()
        }
    }
}
