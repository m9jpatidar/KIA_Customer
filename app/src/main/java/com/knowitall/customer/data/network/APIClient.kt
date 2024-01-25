package com.knowitall.customer.data.network

import android.content.Context
import android.content.SharedPreferences
import com.booozie.data.network.APIInterface
import com.knowitall.customer.AppConstant
import com.knowitall.customer.MainApplication
import com.knowitall.customer.common.SharedPrefsConstant
import com.knowitall.customer.utils.get
import com.knowitall.customer.utils.md5
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object APIClient {

    private var retrofit = Retrofit.Builder()
        .baseUrl(AppConstant.BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(getHttpClient())
        .build()

    fun getAPIClient(): APIInterface {
        return retrofit.create<APIInterface>(APIInterface::class.java)
    }

    private fun getHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient()
            .newBuilder() //.cache(new Cache(MvpApplication.getInstance().getCacheDir(), 10 * 1024 * 1024)) // 10 MB
            .connectTimeout(10, TimeUnit.SECONDS)
            .addNetworkInterceptor(AddHeaderInterceptor())
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(interceptor)
            .build()
    }

    private class AddHeaderInterceptor : Interceptor {
        var sharedPreferences: SharedPreferences = MainApplication.ctx!!.getSharedPreferences(
            SharedPrefsConstant.SHARED_PREFERENCE_FILE_NAME,
            Context.MODE_PRIVATE
        )

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val builder = chain.request().newBuilder()
            val timestamp = System.currentTimeMillis().toString()
            val token = sharedPreferences.get(SharedPrefsConstant.AUTH_TOKEN, "")
//            val userId = sharedPreferences.get(SharedPrefsConstant.USER_ID, "")
//            val userType = sharedPreferences.get(SharedPrefsConstant.USER_TYPE, "")
//            val hashString = "knowitall_customer" + userId + "_" + timestamp
//            builder.addHeader("X-Requested-With", "XMLHttpRequest")
            builder.addHeader("Content-Type", "application/json")
//            builder.addHeader("id", userId)
//            builder.addHeader("timestamp", timestamp)
            builder.addHeader("Authorization", token)
//            builder.addHeader("type", userType)
            return chain.proceed(builder.build())
        }
    }
}
