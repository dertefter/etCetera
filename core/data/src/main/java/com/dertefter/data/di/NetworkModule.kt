package com.dertefter.data.di

import android.util.Log
import com.dertefter.data.datasource.local.TokenManager
import com.dertefter.data.datasource.remote.ApiService
import com.google.gson.Gson
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.java.net.cookiejar.JavaNetCookieJar
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    @Provides
    @Singleton
    fun provideCookieManager(): CookieManager {
        return CookieManager().apply {
            setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        }
    }

    @Provides
    @Singleton
    fun provideCookieJar(
        cookieManager: CookieManager,
        tokenManager: TokenManager
    ): CookieJar {
        val delegate = JavaNetCookieJar(cookieManager)
        return object : CookieJar {
            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                delegate.saveFromResponse(url, cookies)
                val refreshCookies = cookies.filter { it.name == "refresh_token" }
                if (refreshCookies.isNotEmpty()) {
                    val newToken = refreshCookies.find { it.value.isNotBlank() }?.value
                    runBlocking {
                        if (newToken != null) {
                            tokenManager.saveRefreshToken(newToken)
                        } else {
                            tokenManager.deleteRefreshToken()
                        }
                    }
                }
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                val cookies = delegate.loadForRequest(url).toMutableList()
                if (url.encodedPath.contains("api/v1/auth")) {
                    val refreshToken = runBlocking { tokenManager.getRefreshToken() }
                    if (!refreshToken.isNullOrBlank()) {
                        val existing = cookies.find { it.name == "refresh_token" }
                        if (existing == null || existing.value.isBlank()) {
                            cookies.removeAll { it.name == "refresh_token" }
                            cookies.add(
                                Cookie.Builder()
                                    .name("refresh_token")
                                    .value(refreshToken)
                                    .domain(url.host)
                                    .path("/")
                                    .secure()
                                    .httpOnly()
                                    .build()
                            )
                        }
                    }
                }
                return cookies
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        cookieJar: CookieJar,
        tokenManager: TokenManager,
        apiService: Lazy<ApiService>
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                if (request.url.encodedPath.contains("api/v1/auth")) {
                    return@addInterceptor chain.proceed(request)
                }
                val accessToken = runBlocking { tokenManager.getAccessToken() }
                val authenticatedRequest = request.newBuilder().apply {
                    accessToken?.let { addHeader("Authorization", "Bearer $it") }
                }.build()
                chain.proceed(authenticatedRequest)
            }
            .addInterceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)

                if (response.isSuccessful && (request.url.encodedPath.contains("api/v1/auth/sign-in") || request.url.encodedPath.contains("api/v1/auth/refresh"))) {
                    val bodyString = response.peekBody(1024 * 1024).string()
                    try {
                        val signInResponse = Gson().fromJson(bodyString, com.dertefter.data.dto.auth.SignInResponse::class.java)
                        signInResponse?.accessToken?.let { token ->
                            runBlocking { tokenManager.saveAccessToken(token) }
                        }
                    } catch (_: Exception) {
                        // ignore
                    }
                }
                response
            }
            .authenticator { _, response ->
                if (response.request.url.encodedPath.contains("api/v1/auth/refresh")) {
                    return@authenticator null
                }

                synchronized(this) {
                    val accessToken = runBlocking { tokenManager.getAccessToken() }

                    if (response.request.header("Authorization") != "Bearer $accessToken") {
                        Log.d("NetworkModule", "Authenticator: token already updated by another thread, retrying request")
                        return@authenticator response.request.newBuilder()
                            .header("Authorization", "Bearer $accessToken")
                            .build()
                    }

                    val refreshResponse = runBlocking {
                        apiService.get().refreshToken()
                    }

                    if (refreshResponse.isSuccessful) {
                        val newAccessToken = refreshResponse.body()?.accessToken
                        if (newAccessToken != null) {
                            runBlocking { tokenManager.saveAccessToken(newAccessToken) }
                            return@authenticator response.request.newBuilder()
                                .header("Authorization", "Bearer $newAccessToken")
                                .build()
                        }
                    } else {
                        Log.e("NetworkModule", "Authenticator: refreshToken failed with code ${refreshResponse.code()}")
                    }
                    null
                }
            }
            .cookieJar(cookieJar)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://xn--d1ah4a.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}