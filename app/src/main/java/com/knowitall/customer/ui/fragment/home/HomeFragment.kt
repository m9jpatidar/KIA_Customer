package com.knowitall.customer.ui.fragment.home

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.knowitall.customer.base.BaseFragment
import com.knowitall.customer.databinding.FragmentHomeBinding
import com.knowitall.customer.ui.activity.profile.ProfileActivity
import com.knowitall.customer.ui.activity.sendrequest.SendRequestActivity


class HomeFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentHomeBinding
    private var permissionLauncher: ActivityResultLauncher<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            val intent = Intent(activity, SendRequestActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tiktok.setOnClickListener(this)
        binding.facebook.setOnClickListener(this)
        binding.twitter.setOnClickListener(this)
        binding.youtube.setOnClickListener(this)
        binding.instagram.setOnClickListener(this)
        binding.helpme.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            binding.instagram.id -> {
                val likeIng = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/_u/mr.know_it_all_towing")
                )
                likeIng.setPackage("com.instagram.android")

                try {
                    startActivity(likeIng)
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/mr.know_it_all_towing")
                        )
                    )
                }

            }
            binding.facebook.id -> {
                try {
                    context!!.packageManager.getPackageInfo("com.facebook.katana", 0)
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("fb://page/MRKNOWITALLTOWING")
                        )
                    )
                } catch (e: java.lang.Exception) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.facebook.com/MRKNOWITALLTOWING")
                        )
                    )
                }
            }
            binding.twitter.id -> {
                try {
                    context!!.packageManager.getPackageInfo("com.twitter.android", 0)
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("twitter://user?user_id=USERID")
                        )
                    )
                } catch (e: java.lang.Exception) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://twitter.com/PROFILENAME")
                        )
                    )
                }
            }
            binding.tiktok.id -> {
                try {
                    context!!.packageManager.getPackageInfo("com.zhiliaoapp.musically", 0)
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.tiktok.com/@mr_know_it_all_towing")
                        )
                    )
                } catch (e: java.lang.Exception) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.tiktok.com/@mr_know_it_all_towing")
                        )
                    )
                }
            }
            binding.youtube.id -> {
                try {
                    context!!.packageManager.getPackageInfo("com.google.android.youtube", 0)
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/@mrknowitalltowing2258")
                        )
                    )
                } catch (e: java.lang.Exception) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/@mrknowitalltowing2258")
                        )
                    )
                }
            }
            binding.helpme.id -> {
                permissionLauncher?.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
}