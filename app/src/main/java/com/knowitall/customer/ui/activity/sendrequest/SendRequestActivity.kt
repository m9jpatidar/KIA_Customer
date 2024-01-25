package com.knowitall.customer.ui.activity.sendrequest

import android.Manifest
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.clipitc.ClipitcNewMobile.util.GpsHelper
import com.knowitall.customer.R
import com.knowitall.customer.base.BaseActivity
import com.knowitall.customer.common.SharedPrefsConstant
import com.knowitall.customer.databinding.ActivitySendRequestBinding
import com.knowitall.customer.ui.activity.editaddress.EditAddressActivity
import com.knowitall.customer.utils.get


class SendRequestActivity : BaseActivity() {

    private lateinit var binding: ActivitySendRequestBinding
    private var permissionLauncher: ActivityResultLauncher<String>? = null
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var location: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        location = GpsHelper(this).getLocation()

        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                binding.addAddress.text = "qwhjk"
            }
        }

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                val intent = Intent(this, EditAddressActivity::class.java)
                launcher.launch(intent)
            }
        }

        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            resources.getStringArray(R.array.service_type)
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spService.setAdapter(adapter)
        binding.nameEdittext.setText(sharedPreferences.get(SharedPrefsConstant.USER_NAME, ""))
        binding.phoneEdittext.setText(sharedPreferences.get(SharedPrefsConstant.USER_PHONE, ""))

        if (this::location.isInitialized && location.latitude != 0.0) {
            Log.d("gps Location: ", "" + location.longitude + "  --  " + location.latitude)
            val address = getAddressFromLatLng(location.latitude, location.longitude)
            binding.addAddress.text = address?.getAddressLine(0) ?: "No Address"
            binding.editAddress.visibility = View.VISIBLE
        }
    }

    fun onBackPress(view: View) {
        finish()
    }

    fun onAddAddressClick(view: View) {
        permissionLauncher?.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun showProgress() {
        binding.progressBar.progressBarWrapper.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        binding.progressBar.progressBarWrapper.visibility = View.GONE
    }

    fun onSubmit(view: View) {
        val intent = Intent(this, EditAddressActivity::class.java)
        startActivity(intent)
        finish()
    }
}