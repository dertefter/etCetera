package com.dertefter.design.components.post

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import java.io.File

@UnstableApi
object VideoCache {
    private var cache: SimpleCache? = null

    private fun getCache(context: Context): SimpleCache {
        if (cache == null) {
            val cacheDir = File(context.cacheDir, "video_cache")
            val evictor = LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024) // 100MB
            val databaseProvider = StandaloneDatabaseProvider(context)
            cache = SimpleCache(cacheDir, evictor, databaseProvider)
        }
        return cache!!
    }

    fun createMediaSourceFactory(context: Context): DefaultMediaSourceFactory {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(getCache(context))
            .setUpstreamDataSourceFactory(httpDataSourceFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        return DefaultMediaSourceFactory(context)
            .setDataSourceFactory(cacheDataSourceFactory)
    }

    fun createLoadControl(): DefaultLoadControl {
        return DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                20000, // minBufferMs (default is 15000-50000)
                50000, // maxBufferMs
                1500,  // bufferForPlaybackMs (default is 2500)
                2000   // bufferForPlaybackAfterRebufferMs (default is 5000)
            )
            .build()
    }
}
