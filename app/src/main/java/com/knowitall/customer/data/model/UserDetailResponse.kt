package com.knowitall.customer.data.model

import com.google.gson.annotations.SerializedName

data class UserDetailResponse(
    @SerializedName("status")                   var status: Boolean,
    @SerializedName("message")                  var message: String,
    @SerializedName("payload")                  var payload: UserDetail
)

data class UserDetail(
    // if failed
    @SerializedName("message")                  var message: String?,
    @SerializedName("code")                     var code: Int?,
    // if success
    @SerializedName("phoneNumber")              var phoneNumber: String,
    @SerializedName("fullPhoneNumber")          var fullPhoneNumber: String,
    @SerializedName("countryCode")              var countryCode: String,
    @SerializedName("email")                    var email: String,
    @SerializedName("name")                     var name: String,
    @SerializedName("customerId")               var customerId: String
)