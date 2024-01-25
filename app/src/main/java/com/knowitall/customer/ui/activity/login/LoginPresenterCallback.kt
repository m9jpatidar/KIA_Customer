package com.knowitall.customer.ui.activity.login

import com.knowitall.customer.data.model.UserDetailResponse

interface LoginPresenterCallback {

    fun onUserDetailError(error: Throwable)

    fun onUserDetailsFatched(userDetailResponse: UserDetailResponse)
}