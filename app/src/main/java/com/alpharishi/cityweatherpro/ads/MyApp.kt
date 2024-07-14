package com.alpharishi.cityweatherpro.ads

import android.app.Application
import com.google.android.gms.ads.MobileAds

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            MobileAds.initialize(this) {
            }
            AppOpenAdManager(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}