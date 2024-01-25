package com.knowitall.customer.ui.activity.main

import com.knowitall.customer.base.BasePresenter
import com.knowitall.customer.data.network.APIClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainPresenter(
    val activity: MainPresenterCallback
): BasePresenter() {
    val TAG = MainPresenter::class.simpleName

//    fun updateFCM(params: HashMap<String, String>) {
//        composite.add(
//            APIClient
//                .getAPIClient().updateFCM(params)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    { result ->
//                        run {
//                            activity.onUpdateToken(result)
//                        }
//                    },
//                    { error ->
//                        run {
//                            activity.onError(error)
//                        }
//                    }
//                )
//        )
//    }

    fun dispose() {
        composite.dispose()
    }
}