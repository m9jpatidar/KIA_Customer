package com.knowitall.customer.base

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.clipitc.ClipitcNewMobile.util.GpsHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.knowitall.customer.common.SharedPrefsConstant
import com.knowitall.customer.R
import com.knowitall.customer.ui.activity.login.LoginActivity
import com.knowitall.customer.utils.put
import java.io.IOException
import java.util.*

//import com.razorpay.Checkout

abstract class BaseActivity : AppCompatActivity() {
    //    var checkout: Checkout? = null
    lateinit var sharedPreferences: SharedPreferences
    private val TAG: String? = BaseActivity::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        sharedPreferences = this.getSharedPreferences(
            SharedPrefsConstant.SHARED_PREFERENCE_FILE_NAME,
            Context.MODE_PRIVATE
        )

//        checkout = Checkout()
//        Checkout.preload(applicationContext)
//        checkout!!.setKeyID("rzp_test_NaTSufduIgOoe7")
    }

    fun applyTheme() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                theme.applyStyle(R.style.AppTheme, true)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                theme.applyStyle(R.style.AppTheme, true)
            }
            else -> {
                theme.applyStyle(R.style.AppTheme, true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    @ColorInt
    fun getColorFromAttr(
        @AttrRes attrColor: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
    ): Int {
        theme.resolveAttribute(attrColor, typedValue, resolveRefs)
        return typedValue.data
    }

    abstract fun showProgress()

    abstract fun hideProgress()

    fun hideSoftKeyboard() {
        currentFocus?.let {
            val inputMethodManager =
                ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    fun getAddressFromLatLng(latitude: Double, longitude: Double): Address? {
        val geocoder = Geocoder(this, Locale.getDefault())
        var address = null

        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                val address: Address = addresses[0]
                val stringBuilder = StringBuilder()

                for (i in 0 until address.maxAddressLineIndex) {
                    stringBuilder.append(address.getAddressLine(i)).append("\n")
                }
                var addressText = stringBuilder.toString()
                return address
            }
        } catch (e: IOException) {
            Log.d("Address Exception:", e.message ?: "")
        }
        return address
    }
}