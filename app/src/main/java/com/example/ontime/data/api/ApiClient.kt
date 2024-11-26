package com.example.ontime.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiClient @Inject constructor() {
//    private val BASE_URL = "http://10.0.2.2:8080"


//    // 로깅 인터셉터 설정
//    private val loggingInterceptor = HttpLoggingInterceptor().apply {
    // 개발 시에만 BODY, 배포 시에는 NONE
//        level = if (BuildConfig.DEBUG) {
//            HttpLoggingInterceptor.Level.BODY
//        } else {
//            HttpLoggingInterceptor.Level.NONE
//        }
//    }
//
//    // 인증 인터셉터 설정
//    private val authInterceptor = Interceptor { chain ->
//        val request = chain.request().newBuilder()
//            .addHeader("Authorization", "Bearer ${AuthManager.getAccessToken()}")
//            .build()
//        chain.proceed(request)
//    }

//    private val okHttpClient = OkHttpClient.Builder()
//        .addInterceptor(authInterceptor)
//        .addInterceptor(loggingInterceptor)
//        .connectTimeout(30, TimeUnit.SECONDS)
//        .readTimeout(30, TimeUnit.SECONDS)
//        .writeTimeout(30, TimeUnit.SECONDS)
//        .build()

    private val retrofit =
        Retrofit.Builder().baseUrl(BASE_URL)
//            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            ).build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
    val friendApi: FriendApi = retrofit.create(FriendApi::class.java)


//    private val NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org/"


    private val nominatimRetrofit = Retrofit.Builder()
        .baseUrl(NOMINATIM_BASE_URL)
        .client(createNominatimOkHttpClient())
        .addConverterFactory(
            GsonConverterFactory.create()
        ).build()


    val nominatimApi: NominatimApi = nominatimRetrofit.create(NominatimApi::class.java)

    private fun createNominatimOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "onTime")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8080"
        private const val NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org/"
    }

}