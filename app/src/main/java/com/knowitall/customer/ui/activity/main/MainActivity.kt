package com.knowitall.customer.ui.activity.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.knowitall.customer.R
import com.knowitall.customer.base.BaseActivity
import com.knowitall.customer.common.SharedPrefsConstant
import com.knowitall.customer.databinding.ActivityMainBinding
import com.knowitall.customer.ui.activity.login.LoginActivity
import com.knowitall.customer.utils.get
import com.knowitall.customer.utils.put

class MainActivity : BaseActivity(), MainPresenterCallback {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var presenter: MainPresenter
    private lateinit var bindng: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindng = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindng.root)

        presenter = MainPresenter(this)

//        val navView: NavigationView = findViewById(R.id.nav_view)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_requests,
                R.id.nav_my_account,
                R.id.nav_call_directly,
                R.id.nav_promo_code,
                R.id.nav_tnc,
                R.id.nav_faq,
                R.id.nav_signout
            ), bindng.drawerLayout
        )

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TAG", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                sharedPreferences.put(SharedPrefsConstant.AUTH_TOKEN, "Bearer ${task.result?.token.toString()}")
            })

//        setupActionBarWithNavController(navController, appBarConfiguration)
        bindng.navView.setupWithNavController(navController)

        bindng.appBarMain.drawer.setOnClickListener {
            if(bindng.drawerLayout.isDrawerOpen(GravityCompat.START)){
                bindng.drawerLayout.closeDrawer(GravityCompat.START);
            }
            else {
                bindng.drawerLayout.openDrawer(GravityCompat.START);
            }
        }

        val logout = bindng.navView.menu.findItem(R.id.nav_signout)

        logout.setOnMenuItemClickListener {
            FirebaseAuth.getInstance().signOut()
            sharedPreferences.edit().clear().apply()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            ActivityCompat.startActivity(this, intent, null)
            finish()
            false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun showProgress() {
        TODO("Not yet implemented")
    }

    override fun hideProgress() {
        TODO("Not yet implemented")
    }
}