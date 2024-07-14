package com.alpharishi.cityweatherpro.utils

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.alpharishi.cityweatherpro.R
import com.alpharishi.cityweatherpro.databinding.ArDialogRateusBinding

class RatingDialog(private val context: Context) : Dialog(context) {
    private val close: ImageView
    private var binding: ArDialogRateusBinding
    private val starsImgView = arrayOfNulls<ImageView>(5)
    private val star1rdialog: ImageView
    private val star2rdialog: ImageView
    private val star3rdialog: ImageView
    private val star4rdialog: ImageView
    private val star5rdialog: ImageView
    private var starRatingBar: LinearLayout
    private var noOfStar = 0
    private var count = 0
    private var sp: SharedPreferences

    init {
        setContentView(R.layout.ar_dialog_rateus)
        window?.setBackgroundDrawable(ColorDrawable(0))
        window?.setLayout(-1, -1)
        window?.setGravity(Gravity.BOTTOM)

        binding = ArDialogRateusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCanceledOnTouchOutside(false)

        sp = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        count = sp.getInt("SubmitClick", 0)

        star1rdialog = binding.rate1star
        star2rdialog = binding.rate2star
        star3rdialog = binding.rate3star
        star4rdialog = binding.rate4star
        star5rdialog = binding.rate5star
        close = binding.ArBclose

        setupStarBarDialogRating()

        starRatingBar = findViewById(R.id.ratingBar)
        starsImgView[0] = star1rdialog
        starsImgView[1] = star2rdialog
        starsImgView[2] = star3rdialog
        starsImgView[3] = star4rdialog
        starsImgView[4] = star5rdialog
        noOfStar = 3

        close.setOnClickListener {
            dismiss()
        }

        binding.dialogConformBtnShare.setOnClickListener {

            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
            val appname = context.resources.getString(R.string.app_name)
            var shareMessage = context.resources.getString(R.string.AR_share_text) + "\n\n"
            shareMessage += "https://play.google.com/store/apps/details?id=" + context.packageName
            share.putExtra(Intent.EXTRA_TEXT, shareMessage)
            context.startActivity(Intent.createChooser(share, "share"))
            dismiss()
        }

        sp = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

        binding.dialogConformBtnRateus.setOnClickListener {

            if (count < 2) {
                val cc = count + 1
                val edit = sp.edit()
                edit.putInt("SubmitClick", cc)
                edit.apply()
            }
            if (noOfStar > 3) {
                Toast.makeText(context, R.string.AR_rating_good, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, R.string.AR_rating, Toast.LENGTH_SHORT).show()
            }
            openMarket()
            dismiss()
        }

        star1rdialog.setOnClickListener {
            star1rdialog.setImageResource(R.drawable.starselected)
            star2rdialog.setImageResource(R.drawable.starunfilled)
            star3rdialog.setImageResource(R.drawable.starunfilled)
            star4rdialog.setImageResource(R.drawable.starunfilled)
            star5rdialog.setImageResource(R.drawable.starunfilled)
            noOfStar = 1
            setupStarBarDialogRating()
        }
        star2rdialog.setOnClickListener {
            star1rdialog.setImageResource(R.drawable.starselected)
            star2rdialog.setImageResource(R.drawable.starselected)
            star3rdialog.setImageResource(R.drawable.starunfilled)
            star4rdialog.setImageResource(R.drawable.starunfilled)
            star5rdialog.setImageResource(R.drawable.starunfilled)
            noOfStar = 2
            setupStarBarDialogRating()
        }
        star3rdialog.setOnClickListener {
            star5rdialog.setImageResource(R.drawable.starunfilled)
            star4rdialog.setImageResource(R.drawable.starunfilled)
            star3rdialog.setImageResource(R.drawable.starselected)
            star2rdialog.setImageResource(R.drawable.starselected)
            star1rdialog.setImageResource(R.drawable.starselected)
            noOfStar = 3
            setupStarBarDialogRating()
        }
        star4rdialog.setOnClickListener {
            star5rdialog.setImageResource(R.drawable.starunfilled)
            star4rdialog.setImageResource(R.drawable.starselected)
            star3rdialog.setImageResource(R.drawable.starselected)
            star2rdialog.setImageResource(R.drawable.starselected)
            star1rdialog.setImageResource(R.drawable.starselected)
            noOfStar = 4
            setupStarBarDialogRating()
        }
        star5rdialog.setOnClickListener {
            star1rdialog.setImageResource(R.drawable.starselected)
            star2rdialog.setImageResource(R.drawable.starselected)
            star3rdialog.setImageResource(R.drawable.starselected)
            star4rdialog.setImageResource(R.drawable.starselected)
            star5rdialog.setImageResource(R.drawable.starselected)
            noOfStar = 5
            setupStarBarDialogRating()
        }
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        window?.setBackgroundDrawable(ColorDrawable(0))
        window?.setLayout(-1, -2)
    }

    private fun setupStarBarDialogRating() {
        Log.e("============>", "setStarBar: $noOfStar")
    }

    private fun openMarket() {
        val packageName = context.packageName
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("market://details?id=$packageName")
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            context.startActivity(intent)
        }
    }
}