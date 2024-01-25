package com.knowitall.customer.ui.activity.profile

import com.knowitall.customer.base.BasePresenter
import com.knowitall.customer.common.SharedPrefsConstant
import com.knowitall.customer.data.network.APIClient
import com.knowitall.customer.ui.activity.main.MainPresenter
import com.knowitall.customer.ui.activity.main.MainPresenterCallback
import com.knowitall.customer.utils.get
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ProfilePresenter(
    val activity: ProfilePresenterCallback
): BasePresenter() {
    val TAG = ProfilePresenterCallback::class.simpleName

    fun setCreateCustomerDetail(params: HashMap<String, String>) {
        composite.add(
            APIClient
                .getAPIClient().setCreateCustomerDetail(sharedPreferences.get(SharedPrefsConstant.AUTH_TOKEN, ""), params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        run {
                            activity.onSuccessfullProfileUpdate(result)
                        }
                    },
                    { error ->
                        run {
                            activity.onError(error)
                        }
                    }
                )
        )
    }

    fun dispose() {
        composite.dispose()
    }
}