package io.multiservicebot.vk.network.vkapi

import io.multiservicebot.vk.network.util.EnumConverterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object VkApiFactory {
    private const val BASE_URL = "https://api.vk.com/method/"
    private const val DEFAULT_VERSION_API = "5.131"

    fun buildService(token: String, version: String = DEFAULT_VERSION_API): VkApiService {
        val client = OkHttpClient()
            .newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor {
                val original = it.request()

                val url = original.url.newBuilder()
                    .addQueryParameter("access_token", token)
                    .addQueryParameter("v", version)
                    .build()

                it.proceed(original.newBuilder().url(url).build())
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(EnumConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(VkApiService::class.java)
    }
}