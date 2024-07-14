package com.alpharishi.cityweatherpro.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.alpharishi.cityweatherpro.api.weatherApi
import com.alpharishi.cityweatherpro.interfaceApi.InterfaceAPI
import com.alpharishi.cityweatherpro.R
import com.alpharishi.cityweatherpro.utils.RatingDialog
import com.alpharishi.cityweatherpro.databinding.ActivityMainBinding
import com.alpharishi.cityweatherpro.databinding.ArDialogExitBinding
import com.alpharishi.cityweatherpro.databinding.ArDialogInternetBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    private var rateFirstGlob = false
    private var globrate = false
    private var spshowTotalHomeMain = 0
    private var exitDialog: AlertDialog? = null
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var MY_REQUEST_CODE = 111
    private var appUpdateManagerMain: AppUpdateManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.ThemeAppCompatNoActionBar, true)
        setContentView(binding.root)

        searchCity()
        if (!isInternetAvailable()) {
            showNoInternetDialog()
        }
        fetchWeatherDataOfCity("Ahmedabad")

        if (Build.VERSION.SDK_INT > 32) {
            homeShowRating()
            updateDialogMain()
            homeShowRating()
            if (spshowTotalHomeMain == 2) {
                globrate = true
                rateFirstGlob = false
                RatingDialog(this).show()
            }
        } else {
            updateDialogMain()
            homeShowRating()
            if (spshowTotalHomeMain == 2) {
                globrate = true
                rateFirstGlob = false
                RatingDialog(this).show()
            }
        }

    }

    private fun searchCity() {
        val search = binding.searchView
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherDataOfCity(query.trim())
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun homeShowRating() {
        Log.e("qqqq", "=====homeShowRating========11=")
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        var spshowTotalHomeMain = sharedPreferences.getInt("counterHome", 0)
        spshowTotalHomeMain++
        editor.putInt("counterHome", spshowTotalHomeMain)
        editor.apply()
    }

    private var listener = InstallStateUpdatedListener { installStateMain: InstallState ->
        if (installStateMain.installStatus() == InstallStatus.DOWNLOADING) {
            val bytesDownloaded = installStateMain.bytesDownloaded()
            val totalBytesToDownload = installStateMain.totalBytesToDownload()
        }
        if (installStateMain.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackbarForCompleteUpdate()
        }
    }

    private fun popupSnackbarForCompleteUpdate() {
        val sb1 = Snackbar.make(
            findViewById(R.id.mainvie),
            "App Update Almost done.",
            Snackbar.LENGTH_INDEFINITE
        )
        sb1.setAction("RESTART") { appUpdateManagerMain?.completeUpdate() }
        sb1.setActionTextColor(
            resources.getColor(R.color.black)
        )
        sb1.show()
    }

    private fun updateDialogMain() {
        val appUpdateManagerMain = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManagerMain.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) || appUpdateInfo.isUpdateTypeAllowed(
                    AppUpdateType.FLEXIBLE
                ))
            ) {
                try {
                    appUpdateManagerMain.startUpdateFlowForResult(
                        appUpdateInfo,
                        this,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE)
                            .setAllowAssetPackDeletion(true)
                            .build(),
                        MY_REQUEST_CODE
                    )
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
        appUpdateManagerMain.registerListener(listener)
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return connectivityManager?.activeNetworkInfo?.isConnected ?: false
    }

    private fun showNoInternetDialog() {
        val dialogBinding = ArDialogInternetBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogBinding.root)
        val dialog = builder.create()
        dialog.show()

        dialogBinding.ArBclose.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.btnTryAgain.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun fetchWeatherDataOfCity(city: String) {
        val rt = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build()
            .create(InterfaceAPI::class.java)

//          USE YOUR OWN API KEY

        val res = rt.getWeatherData(city, "XX---API KEY----XX", "metric")
        res.enqueue(object : Callback<weatherApi> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<weatherApi>, response: Response<weatherApi>) {
                val resBody = response.body()
                if (response.isSuccessful && resBody != null) {
                    Log.d("TAG", "onResponse: getting values")

//                    setting values to vars
                    val temperature = resBody.main.temp.roundToInt().toString()
                    val humid = resBody.main.humidity
                    val windSpd = resBody.wind.speed
                    val morning = resBody.sys.sunrise
                    val evening = resBody.sys.sunset
                    val seaL = resBody.main.pressure
                    val min = resBody.main.temp_min.roundToInt().toString()
                    val max = resBody.main.temp_max.roundToInt().toString()
                    val cond = resBody.weather.firstOrNull()?.main ?: "unknown"

//                    set values
                    binding.tvTemp.text = "$temperature°C"
                    binding.tvHumadity.text = "$humid %"
                    binding.tvWindSpeed.text = "$windSpd m/s"
                    binding.tvSunrise.text = getTime(morning.toLong())
                    binding.tvSunset.text = getTime(evening.toLong())
                    binding.tvSeaLvl.text = "$seaL hPa"
//                    binding.tvMinTemp.text = "Min Temp: $min °C"
//                    binding.tvMaxTemp.text = "Max Temp: $max °C"
                    binding.tvWeatherCond.text = cond
                    binding.tvWeather.text = cond
                    binding.tvDay.text = getDay()
                    binding.tvDate.text = getDate()
                    binding.tvLocation.text = city.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }

                    //                  Set Values from °C to °F
                    val temperatureDouble = resBody.main.temp.roundToInt()
                    val temperatureFahrenheit = celsiusToFahrenheit(temperatureDouble)

                    binding.tvTempF.text = "/ $temperatureFahrenheit°F"

//                    change bg and lottie
                    changeBgLottie(cond)
                    binding.searchView.setQuery("", false) // Clear previous query
                    binding.searchView.clearFocus() // Clear focus to hide keyboard

//======================== hide some data according to display size --------
                    // Get the default display
                    val display: Display = windowManager.defaultDisplay
                    val displayMetrics = DisplayMetrics()

                    // Get display metrics of the default display
                    display.getMetrics(displayMetrics)

                    // Calculate the screen size in inches
                    val screenSizeInches =
                        sqrt(
                            ((displayMetrics.widthPixels / displayMetrics.xdpi).toDouble()).pow(2.0)
                                    + ((displayMetrics.heightPixels / displayMetrics.ydpi).toDouble()).pow(2.0)
                        )

                    // If screen size is less than 5.5 inches, hide the max and min temperature data
                    if (screenSizeInches < 5.8) {
                        binding.tvMinTemp.visibility = TextView.GONE
                        binding.tvMaxTemp.visibility = TextView.GONE
                        binding.textView4.visibility = TextView.GONE
                    }else{
                        binding.tvMinTemp.text = "Min Temp: $min °C"
                        binding.tvMaxTemp.text = "Max Temp: $max °C"
                    }

//  =========================================================================================

                } else {
                    binding.searchView.setQuery("", false) // Clear previous query
                    binding.searchView.clearFocus() // Clear focus to hide keyboard
                    Toast.makeText(
                        this@MainActivity,
                        "Please enter a valid city name.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<weatherApi>, t: Throwable) {
                Log.d("TAG", "onFailure: Failed to get Data")
                Toast.makeText(
                    this@MainActivity,
                    "Failed to fetch data. Please check your internet connection.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun changeBgLottie(condition: String) {
        try {
            when (condition) {
                "Clouds", "Partly Clouds" -> {
                    binding.root.setBackgroundResource(R.drawable.gradient_cloudy)
                    binding.lottie.setAnimation(R.raw.cloudy)
                }

                "Haze", "Overcast" -> {
                    binding.root.setBackgroundResource(R.drawable.gradient_haze)
                    binding.lottie.setAnimation(R.raw.windy)
                }

                "Smoke" -> {
                    binding.root.setBackgroundResource(R.drawable.gradient_smoke)
                    binding.lottie.setAnimation(R.raw.smoke)
                }

                "Mist" -> {
                    binding.root.setBackgroundResource(R.drawable.gradient_mist)
                    binding.lottie.setAnimation(R.raw.mist)
                }

                "Fog", "Foggy" -> {
                    binding.root.setBackgroundResource(R.drawable.gradient_foggy)
                    binding.lottie.setAnimation(R.raw.foggy)
                }

                "Sunny" -> {
                    binding.root.setBackgroundResource(R.drawable.gradient_orange)
                    binding.lottie.setAnimation(R.raw.sunny)
                }

                "Drizzle", "Showers" -> {
                    binding.root.setBackgroundResource(R.drawable.gradient_shower)
                    binding.lottie.setAnimation(R.raw.rain)
                }

                "Rain", "Little Rain", "Moderate Rain", "Heavy Rain" -> {
                    binding.root.setBackgroundResource(R.drawable.gradient_rain)
                    binding.lottie.setAnimation(R.raw.rain)
                }

                "Clear Sky", "Clear" -> {
                    binding.root.setBackgroundResource(R.drawable.gradient_clear)
                    binding.lottie.setAnimation(R.raw.clear)
                }

                "Snow", "Little Snow", "Moderate Snow", "Heavy Snow", "Blizzards" -> {
                    binding.root.setBackgroundResource(R.drawable.gradient_snow)
                    binding.lottie.setAnimation(R.raw.snow)
                }

                else -> {
                    binding.root.setBackgroundResource(R.drawable.gradient_clear)
                    binding.lottie.setAnimation(R.raw.cloudy)
                }
            }
        } catch (e: Exception) {
            e.stackTrace
        }
        binding.lottie.playAnimation()
    }

    @SuppressLint("MissingSuperCall")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        showExitDialog()
    }

    private fun getDate(): String {
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return simpleDateFormat.format(Date())
    }

    private fun getDay(): String {
        val simpleDateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        return simpleDateFormat.format(Date())
    }

    private fun getTime(timestamp: Long): String {
        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return simpleDateFormat.format(Date(timestamp * 1000))
    }

    fun celsiusToFahrenheit(celsius: Int): Int {
        return (celsius * 9 / 5) + 32
    }

    private fun showExitDialog() {
        val dialogBinding = ArDialogExitBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogBinding.root)

        exitDialog = builder.create()
        exitDialog?.show()

        dialogBinding.ArBtnClose.setOnClickListener {
            exitDialog?.dismiss()
            finish() // Close the activity
        }

        dialogBinding.ArBtnRate.setOnClickListener {
            exitDialog?.dismiss()
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=" + this.packageName)
            ).apply {
                this@MainActivity.startActivity(this)
            }// Close the activity
        }

        dialogBinding.ArBclose.setOnClickListener {
            exitDialog?.dismiss()
        }
        exitDialog?.setCanceledOnTouchOutside(false)
    }
}
