package com.knowitall.customer.ui.activity.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.knowitall.customer.R
import com.knowitall.customer.base.BaseActivity
import com.knowitall.customer.common.SharedPrefsConstant
import com.knowitall.customer.data.model.UserDetailResponse
import com.knowitall.customer.databinding.ActivityProfileBinding
import com.knowitall.customer.ui.activity.main.MainActivity
import com.knowitall.customer.utils.get
import com.knowitall.customer.utils.put

class ProfileActivity : BaseActivity(), ProfilePresenterCallback {

    private lateinit var binder: ActivityProfileBinding
    private lateinit var presenter: ProfilePresenter
    private val TAG: String? = ProfileActivity::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binder.root)

        presenter = ProfilePresenter(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.dispose()
    }

    override fun showProgress() {
        binder.progressBar.progressBarWrapper.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        binder.progressBar.progressBarWrapper.visibility = View.GONE
    }

    override fun onError(error: Throwable) {
        hideProgress()
        Snackbar.make(
            binder.loginImg,
            getString(R.string.something_went_wrong),
            Snackbar.LENGTH_SHORT
        ).show()
        Log.d(TAG, "onUserDetailError: " + error.message)
    }

    fun onSubmit(view: View) {
        if (checkValidation()) {
            showProgress()
            val param = hashMapOf(
                "countryCode" to sharedPreferences.get(SharedPrefsConstant.USER_COUNTRY_CODE, ""),
                "phoneNumber" to sharedPreferences.get(SharedPrefsConstant.USER_PHONE, ""),
                "name" to binder.nameEdittext.text.toString(),
                "email" to binder.emailEdittext.text.toString()
            )
            presenter.setCreateCustomerDetail(param)
        }
    }

    private fun checkValidation(): Boolean {
        if (binder.nameEdittext.text.isEmpty()) {
            return false
        }
        if (binder.emailEdittext.text.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(binder.emailEdittext.text)
                .matches()
        ) {
            return false
        }
        return true
    }

    override fun onSuccessfullProfileUpdate(userDetailResponse: UserDetailResponse) {
        hideProgress()
        if (userDetailResponse.status) {
            sharedPreferences.put(
                SharedPrefsConstant.USER_ID,
                userDetailResponse.payload.customerId
            )
            sharedPreferences.put(SharedPrefsConstant.USER_NAME, userDetailResponse.payload.name)
            sharedPreferences.put(SharedPrefsConstant.USER_EMAIL, userDetailResponse.payload.email)
            sharedPreferences.put(
                SharedPrefsConstant.USER_PHONE,
                userDetailResponse.payload.phoneNumber
            )
            sharedPreferences.put(
                SharedPrefsConstant.USER_FULL_PHONE,
                userDetailResponse.payload.fullPhoneNumber
            )
            sharedPreferences.put(
                SharedPrefsConstant.USER_COUNTRY_CODE,
                userDetailResponse.payload.countryCode
            )
            openActivity()
        }
    }

    private fun openActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        ActivityCompat.startActivity(this, intent, null)
        finish()
    }
}