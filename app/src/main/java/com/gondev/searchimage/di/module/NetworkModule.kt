package com.gondev.searchimage.di.module

import android.app.Application
import com.gondev.searchimage.BuildConfig
import com.gondev.searchimage.model.network.api.KakaoImageAPI
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val CONNECT_TIMEOUT = 15L
private const val WRITE_TIMEOUT = 15L
private const val READ_TIMEOUT = 15L

/**
 * Hilt DI 라이브러리에서 사용하는 데이터베이스 모듈입니다
 * ViewModel에 네트워크 콜 서비스를 제공합니다
 */
@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideKakaoImageAPIService(application: Application) =
        Retrofit.Builder()
            .baseUrl(BuildConfig.base_url)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ssSSS")
                        /*.registerTypeAdapter(Date::class.java, object :
                            JsonDeserializer<Date> {
                            override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date? {
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssSSSXXX")
                                dateFormat.timeZone=TimeZone.getTimeZone("UTC")
                                return dateFormat.parse(json.asString)
                            }
                        })*/
                        .create()
                )
            )
            .client(okhttpClient(application))
            .build()
            .create(KakaoImageAPI::class.java)

    private fun okhttpClient(application: Application) =
        OkHttpClient.Builder().apply {
            cache(Cache(application.cacheDir, 10 * 1024 * 1024))
            connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            retryOnConnectionFailure(true)
            addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder().apply {
                    addHeader("Authorization", BuildConfig.apikey)
                }.build())
            }
            addInterceptor(HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            })
        }.build()
}