package com.knowitall.customer.base

import android.content.Context
import androidx.fragment.app.Fragment
import com.knowitall.customer.MainApplication
import com.knowitall.customer.common.SharedPrefsConstant

open class BaseFragment : Fragment() {
    val sharedPreferences = MainApplication.ctx!!.getSharedPreferences(SharedPrefsConstant.SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)
}