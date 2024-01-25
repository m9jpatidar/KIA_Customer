package com.knowitall.customer.ui.activity.splash

import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.knowitall.customer.ui.activity.main.MainActivity
import com.knowitall.customer.base.BaseActivity
import com.knowitall.customer.common.SharedPrefsConstant
import com.knowitall.customer.databinding.ActivitySplashBinding
import com.knowitall.customer.ui.activity.login.LoginActivity
import com.knowitall.customer.utils.get
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GlobalScope.launch {
            delay(3000)
            openActivity()
        }
    }

    private fun openActivity() {
        if (sharedPreferences.get(SharedPrefsConstant.AUTH_TOKEN, "")
                .isNotEmpty() && sharedPreferences.get(SharedPrefsConstant.USER_NAME, "")
                .isNotEmpty()
        ) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            ActivityCompat.startActivity(this, intent, null)
            finish()
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            ActivityCompat.startActivity(this, intent, null)
            finish()
        }
    }

    override fun showProgress() {
    }

    override fun hideProgress() {
    }
}