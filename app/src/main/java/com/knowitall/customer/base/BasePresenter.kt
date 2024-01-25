package com.knowitall.customer.base

import android.content.Context
import android.content.SharedPreferences
import com.knowitall.customer.MainApplication
import com.knowitall.customer.common.SharedPrefsConstant
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.disposables.CompositeDisposable

open class BasePresenter {
    var composite: CompositeDisposable
        get() {
            return if (field.isDisposed) CompositeDisposable() else field
        }
        private set

    var auth: FirebaseAuth
    var sharedPreferences: SharedPreferences

    init {
        composite = CompositeDisposable()
        auth = FirebaseAuth.getInstance()
        sharedPreferences = MainApplication.ctx!!.getSharedPreferences(
            SharedPrefsConstant.SHARED_PREFERENCE_FILE_NAME,
            Context.MODE_PRIVATE
        )
    }
}