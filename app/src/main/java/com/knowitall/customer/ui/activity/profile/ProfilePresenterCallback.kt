package com.knowitall.customer.ui.activity.profile

import com.knowitall.customer.data.model.UserDetailResponse

interface ProfilePresenterCallback {

    fun onError(error: Throwable)

    fun onSuccessfullProfileUpdate(createUserResponse: UserDetailResponse)
}