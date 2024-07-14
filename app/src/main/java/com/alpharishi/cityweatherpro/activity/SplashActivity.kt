package com.alpharishi.cityweatherpro.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.airbnb.lottie.LottieAnimationView
import com.alpharishi.cityweatherpro.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.ThemeAppCompatNoActionBar, true)
        setContentView(R.layout.activity_splash)

        val image : LottieAnimationView = findViewById(R.id.imageview)
        image.playAnimation()
        Handler(Looper.getMainLooper()).postDelayed({
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            finish()
        }, 2500)
    }
}