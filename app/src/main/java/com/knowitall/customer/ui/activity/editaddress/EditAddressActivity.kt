package com.knowitall.customer.ui.activity.editaddress

import android.Manifest
import android.content.Intent
import android.location.Address
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.clipitc.ClipitcNewMobile.util.GpsHelper
import com.knowitall.customer.base.BaseActivity
import com.knowitall.customer.databinding.ActivityEditAddressBinding
import com.knowitall.customer.ui.activity.sendrequest.SendRequestActivity

class EditAddressActivity : BaseActivity() {

    private lateinit var binding: ActivityEditAddressBinding
    private var permissionLauncher: ActivityResultLauncher<String>? = null
    private lateinit var location: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        location = GpsHelper(this).getLocation()

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (this::location.isInitialized) {
                updateAddressDetail(getAddressFromLatLng(location.latitude, location.longitude))
            }
        }
    }

    fun getCurrentLocation(view: View) {
        permissionLauncher?.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun onEditDone(view: View) {
        var intent = Intent(this, SendRequestActivity::class.java)
        intent.putExtra("result", "testing back")
        setResult(RESULT_OK, intent)
        finish()
    }

    fun onBackPress(view: View) {
        finish()
    }

    override fun showProgress() {
        binding.progressBar.progressBarWrapper.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        binding.progressBar.progressBarWrapper.visibility = View.GONE
    }

    private fun updateAddressDetail(address: Address?) {
        binding.addLine1Edittext.setText(address?.getAddressLine(0) ?: "")
        binding.addLine2Edittext.setText(address?.getAddressLine(1) ?: "")
        binding.cityEdittext.setText(address?.locality ?: "")
        binding.stateEdittext.setText(address?.adminArea ?: "")
        binding.postalcodeEdittext.setText(address?.postalCode ?: "")
        binding.landmarkEdittext.setText(address?.subLocality ?: address?.locality ?: "")
    }
}