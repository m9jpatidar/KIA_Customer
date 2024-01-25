package com.knowitall.customer.ui.activity.login

import com.google.firebase.firestore.FirebaseFirestore
import com.knowitall.customer.base.BasePresenter
import com.knowitall.customer.common.SharedPrefsConstant
import com.knowitall.customer.data.network.APIClient
import com.knowitall.customer.utils.get
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class LoginPresenter(
    val activity: LoginPresenterCallback,
    private val firestoreDB: FirebaseFirestore
): BasePresenter() {
    val TAG = LoginPresenter::class.simpleName

    fun getUserDetail() {
        composite.add(
            APIClient
                .getAPIClient().getUserDetail(sharedPreferences.get(SharedPrefsConstant.AUTH_TOKEN, ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        run {
                            activity.onUserDetailsFatched(result)
                        }
                    },
                    { error ->
                        run {
                            activity.onUserDetailError(error)
                        }
                    }
                )
        )
    }
    fun dispose() {
        composite.dispose()
    }
}