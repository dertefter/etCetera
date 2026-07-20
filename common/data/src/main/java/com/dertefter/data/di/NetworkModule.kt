package com.dertefter.data.di

import android.content.Context
import com.dertefter.data.datasource.local.LocalDataSource
import com.dertefter.data.datasource.local.TokenManager
import com.dertefter.data.datasource.remote.ApiService
import com.dertefter.data.dto.auth.SignInRequest
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class InternalAuthApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IsWearDevice

object RequestContext {
    private val ownerLogin = ThreadLocal<String>()
    fun set(login: String?) = ownerLogin.set(login)
    fun get(): String? = ownerLogin.get()
    fun clear() = ownerLogin.remove()
}

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
    fun provideCookieJar(
        tokenManager: TokenManager,
        localDataSource: LocalDataSource,
        @IsWearDevice isWear: Boolean
    ): CookieJar {
        return object : CookieJar {
            private val userCookies = ConcurrentHashMap<String, MutableList<Cookie>>()

            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                val login = RequestContext.get() ?: runBlocking { localDataSource.currentLogin.first() }
                if (login == null) return

                val store = userCookies.getOrPut(login) { mutableListOf() }
                cookies.forEach { newCookie ->
                    store.removeAll { it.name == newCookie.name && it.domain == newCookie.domain }
                    store.add(newCookie)
                }

                if (isWear) return

                val refreshToken = cookies.filter { it.name == "refresh_token" }.lastOrNull { it.value.isNotBlank() }?.value
                if (!refreshToken.isNullOrBlank()) {
                    tokenManager.saveRefreshTokenForLogin(login, refreshToken)
                }
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                val login = RequestContext.get() ?: runBlocking { localDataSource.currentLogin.first() }
                if (login == null) return emptyList()

                val cookies = userCookies[login]?.toMutableList() ?: mutableListOf()
                
                if (url.encodedPath.contains("api/v1/auth")) {
                    val refreshToken = login.let { runBlocking { tokenManager.getRefreshTokenForLogin(it).firstOrNull() } }
                    if (!refreshToken.isNullOrBlank()) {
                        if (cookies.none { it.name == "refresh_token" }) {
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
                        if (cookies.none { it.name == "is_auth" }) {
                            cookies.add(
                                Cookie.Builder()
                                    .name("is_auth")
                                    .value("1")
                                    .domain(url.host)
                                    .path("/")
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
    @InternalAuthApi
    fun provideInternalAuthApiService(
        cookieJar: CookieJar,
        localDataSource: LocalDataSource,
        tokenManager: TokenManager
    ): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)

                val bodyString = response.peekBody(1024 * 1024).string()
                try {
                    val jsonObject = Gson().fromJson(bodyString, com.google.gson.JsonObject::class.java)
                    val errorCode = jsonObject.getAsJsonObject("error")?.get("code")?.asString
                    if (errorCode == "SESSION_NOT_FOUND" || errorCode == "SESSION_REVOKED") {
                        val login = request.tag(String::class.java) ?: runBlocking { localDataSource.currentLogin.first() }
                        login?.let { l ->
                            runBlocking {
                                localDataSource.switchToLogin(null)
                                tokenManager.deleteAccessTokenForLogin(l)
                                tokenManager.deleteRefreshTokenForLogin(l)
                            }
                        }
                    }
                } catch (_: Exception) {}
                response
            }
            .addInterceptor { chain ->
                val request = chain.request()
                var login = request.tag(String::class.java)

                if (request.url.encodedPath.contains("api/v1/auth/sign-in")) {
                    try {
                        val requestBuffer = okio.Buffer()
                        request.body?.writeTo(requestBuffer)
                        login = Gson().fromJson(requestBuffer.readUtf8(), SignInRequest::class.java)?.email
                    } catch (_: Exception) {
                    }
                }

                if (login == null) {
                    login = runBlocking { localDataSource.currentLogin.first() }
                }

                RequestContext.set(login)
                try {
                    chain.proceed(request)
                } finally {
                    RequestContext.clear()
                }
            }
            .cookieJar(cookieJar)
            .build()
        return Retrofit.Builder()
            .baseUrl("https://xn--d1ah4a.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        cookieJar: CookieJar,
        tokenManager: TokenManager,
        localDataSource: LocalDataSource,
        @InternalAuthApi authApiService: Lazy<ApiService>,
        @IsWearDevice isWear: Boolean,
        @ApplicationContext context: Context
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val dispatcher = okhttp3.Dispatcher().apply {
            maxRequests = 64
            maxRequestsPerHost = 64
        }
        return OkHttpClient.Builder()
            .dispatcher(dispatcher)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request()
                var login = request.tag(String::class.java)

                if (request.url.encodedPath.contains("api/v1/auth/sign-in")) {
                    try {
                        val requestBuffer = okio.Buffer()
                        request.body?.writeTo(requestBuffer)
                        login = Gson().fromJson(requestBuffer.readUtf8(), SignInRequest::class.java)?.email
                    } catch (_: Exception) {
                    }
                }

                if (login == null) {
                    login = runBlocking { localDataSource.currentLogin.first() }
                }

                val newRequest = if (request.tag(String::class.java) == null && login != null) {
                    request.newBuilder().tag(String::class.java, login).build()
                } else request

                RequestContext.set(login)
                try {
                    if (newRequest.url.encodedPath.contains("api/v1/auth")) {
                        return@addInterceptor chain.proceed(newRequest)
                    }
                    val accessToken = login?.let { runBlocking { tokenManager.getAccessTokenForLogin(it).firstOrNull() } }
                    val authenticatedRequest = newRequest.newBuilder().apply {
                        accessToken?.let { addHeader("Authorization", "Bearer $it") }
                    }.build()
                    chain.proceed(authenticatedRequest)
                } finally {
                    RequestContext.clear()
                }
            }
            .addInterceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)

                val bodyString = response.peekBody(1024 * 1024).string()
                try {
                    val jsonObject = Gson().fromJson(bodyString, com.google.gson.JsonObject::class.java)
                    val errorCode = jsonObject.getAsJsonObject("error")?.get("code")?.asString
                    if (errorCode == "SESSION_NOT_FOUND" || errorCode == "SESSION_REVOKED") {
                        val login = request.tag(String::class.java) ?: runBlocking { localDataSource.currentLogin.first() }
                        login?.let { l ->
                            runBlocking {
                                localDataSource.switchToLogin(null)
                                tokenManager.deleteAccessTokenForLogin(l)
                                tokenManager.deleteRefreshTokenForLogin(l)
                            }
                        }
                    }
                } catch (_: Exception) {}

                if (isWear) return@addInterceptor response

                val loginFromTag = request.tag(String::class.java)

                if (response.isSuccessful && (request.url.encodedPath.contains("api/v1/auth/sign-in") || request.url.encodedPath.contains("api/v1/auth/refresh"))) {
                    try {
                        val jsonObject = Gson().fromJson(bodyString, com.google.gson.JsonObject::class.java)
                        val login = if (request.url.encodedPath.contains("api/v1/auth/sign-in")) {
                            val requestBuffer = okio.Buffer()
                            request.body?.writeTo(requestBuffer)
                            try {
                                Gson().fromJson(requestBuffer.readUtf8(), SignInRequest::class.java)?.email
                            } catch (_: Exception) { null }
                        } else {
                            loginFromTag
                        }

                        login?.let { l ->
                            jsonObject.get("accessToken")?.asString?.let { token ->
                                runBlocking {
                                    if (request.url.encodedPath.contains("api/v1/auth/sign-in")) {
                                        localDataSource.switchToLogin(l)
                                    }
                                    tokenManager.saveAccessTokenForLogin(l, token)
                                }
                            }
                        }
                    } catch (_: Exception) {}
                }
                response
            }
            .authenticator { _, response ->
                if (isWear) {
                    if (response.request.url.encodedPath.contains("api/v1/auth/refresh")) return@authenticator null

                    val login = response.request.tag(String::class.java) ?: runBlocking { localDataSource.currentLogin.first() }
                    if (login == null) return@authenticator null

                    val currentToken = runBlocking { tokenManager.getAccessTokenForLogin(login).firstOrNull() }

                    val messageClient = Wearable.getMessageClient(context)
                    val nodeClient = Wearable.getNodeClient(context)
                    try {
                        val nodes = Tasks.await(nodeClient.connectedNodes)
                        for (node in nodes) {
                            messageClient.sendMessage(node.id, "/request_token_refresh", byteArrayOf())
                        }
                    } catch (_: Exception) {
                    }

                    val newToken = runBlocking {
                        withTimeoutOrNull(10000.milliseconds) {
                            tokenManager.getAccessTokenForLogin(login)
                                .first { it != null && it != currentToken }
                        }
                    }

                    if (newToken != null) {
                        return@authenticator response.request.newBuilder()
                            .header("Authorization", "Bearer $newToken")
                            .tag(String::class.java, login)
                            .build()
                    }

                    return@authenticator null
                }
                if (response.request.url.encodedPath.contains("api/v1/auth/refresh")) return@authenticator null

                val login = response.request.tag(String::class.java) ?: runBlocking { localDataSource.currentLogin.first() }
                if (login == null) return@authenticator null

                synchronized(this) {
                    val accessToken = runBlocking { tokenManager.getAccessTokenForLogin(login).firstOrNull() }
                    if (response.request.header("Authorization") != "Bearer $accessToken") {
                        return@authenticator response.request.newBuilder()
                            .header("Authorization", "Bearer $accessToken")
                            .tag(String::class.java, login)
                            .build()
                    }

                    val refreshResponse = runBlocking { authApiService.get().refreshToken(login) }
                    if (refreshResponse.isSuccessful) {
                        val body = refreshResponse.body()
                        val newAccessToken = body?.get("accessToken")

                        if (newAccessToken != null) {
                            tokenManager.saveAccessTokenForLogin(login, newAccessToken)
                            return@authenticator response.request.newBuilder()
                                .header("Authorization", "Bearer $newAccessToken")
                                .tag(String::class.java, login)
                                .build()
                        }
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
