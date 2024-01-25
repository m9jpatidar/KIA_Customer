package com.booozie.data.network

import com.knowitall.customer.data.model.UserDetailResponse
import io.reactivex.Observable
import retrofit2.http.*
import java.io.Serializable

interface APIInterface {

    @GET("/dev/customers/me")
    fun getUserDetail(@Header("Authorization") auth: String): Observable<UserDetailResponse>

    @POST("/dev/customers")
    fun setCreateCustomerDetail(@Header("Authorization") auth: String, @Body body: HashMap<String, String>): Observable<UserDetailResponse>
}