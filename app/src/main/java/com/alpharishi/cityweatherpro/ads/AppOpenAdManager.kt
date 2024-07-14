package com.alpharishi.cityweatherpro.ads

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.alpharishi.cityweatherpro.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import java.util.Date

class AppOpenAdManager(private val myApp: MyApp) : ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private var appOpenAd : AppOpenAd? = null
    private var currActivity : Activity? = null
    private var isShowingAd = false
    private var loadTime : Long = 0

    init {
        myApp.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    private fun wasLoadTimeLessThanNHoursAgo(): Boolean {
        val numHours = 4
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo()
    }

    private fun fetchAd(){
        if (isAdAvailable()){
            return
        }
        val loadCallbacks = object : AppOpenAdLoadCallback(){
            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                super.onAdLoaded(appOpenAd)
                this@AppOpenAdManager.appOpenAd = appOpenAd
                loadTime = Date().time
            }
        }
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            myApp,
            currActivity!!.getString(R.string.appOpnAd),
            request,
            loadCallbacks
        )
    }
    private fun showAppOpnAdIfAvailable(){
        if (!isShowingAd && isAdAvailable()){
            appOpenAd!!.fullScreenContentCallback = object : FullScreenContentCallback(){
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    isShowingAd = false
                    fetchAd()
                }

                override fun onAdShowedFullScreenContent() {
                    isShowingAd = true
                }
            }

            appOpenAd!!.show(currActivity!!)
        }else{
            fetchAd()
        }
    }
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {    }

    override fun onActivityStarted(activity: Activity) {
        if (!isShowingAd){
            currActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {
        if (!isShowingAd){
            currActivity = activity
        }
    }

    override fun onActivityPaused(activity: Activity) {    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        currActivity = null
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        showAppOpnAdIfAvailable()
    }

}