package com.strimm.application;

import android.content.Context;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.database.DatabaseProvider;
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

/* loaded from: classes.dex */
public final class DemoUtil {
    private static final String DOWNLOAD_CONTENT_DIRECTORY = "downloads";
    private static final String TAG = "DemoUtil";
    private static DataSource.Factory dataSourceFactory;
    private static DatabaseProvider databaseProvider;
    private static Cache downloadCache;
    private static File downloadDirectory;
    private static DataSource.Factory httpDataSourceFactory;

    public static RenderersFactory buildRenderersFactory(Context context, boolean z) {
        return new DefaultRenderersFactory(context.getApplicationContext()).setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON);
    }

    public static synchronized DataSource.Factory getHttpDataSourceFactory(Context context) {
        DataSource.Factory factory;
        synchronized (DemoUtil.class) {
            if (httpDataSourceFactory == null) {
                CookieManager cookieManager = new CookieManager();
                cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
                CookieHandler.setDefault(cookieManager);
                httpDataSourceFactory = new DefaultHttpDataSource.Factory();
            }
            factory = httpDataSourceFactory;
        }
        return factory;
    }

    public static synchronized DataSource.Factory getDataSourceFactory(Context context) {
        DataSource.Factory factory;
        synchronized (DemoUtil.class) {
            if (dataSourceFactory == null) {
                Context applicationContext = context.getApplicationContext();
                dataSourceFactory = buildReadOnlyCacheDataSource(new DefaultDataSource.Factory(applicationContext, getHttpDataSourceFactory(applicationContext)), getDownloadCache(applicationContext));
            }
            factory = dataSourceFactory;
        }
        return factory;
    }

    private static synchronized Cache getDownloadCache(Context context) {
        Cache cache;
        synchronized (DemoUtil.class) {
            if (downloadCache == null) {
                downloadCache = new SimpleCache(new File(getDownloadDirectory(context), DOWNLOAD_CONTENT_DIRECTORY), new NoOpCacheEvictor(), getDatabaseProvider(context));
            }
            cache = downloadCache;
        }
        return cache;
    }

    private static synchronized DatabaseProvider getDatabaseProvider(Context context) {
        DatabaseProvider databaseProvider2;
        synchronized (DemoUtil.class) {
            if (databaseProvider == null) {
                databaseProvider = new StandaloneDatabaseProvider(context);
            }
            databaseProvider2 = databaseProvider;
        }
        return databaseProvider2;
    }

    private static synchronized File getDownloadDirectory(Context context) {
        File file;
        synchronized (DemoUtil.class) {
            if (downloadDirectory == null) {
                File externalFilesDir = context.getExternalFilesDir(null);
                downloadDirectory = externalFilesDir;
                if (externalFilesDir == null) {
                    downloadDirectory = context.getFilesDir();
                }
            }
            file = downloadDirectory;
        }
        return file;
    }

    private static CacheDataSource.Factory buildReadOnlyCacheDataSource(DataSource.Factory factory, Cache cache) {
        return new CacheDataSource.Factory().setCache(cache).setUpstreamDataSourceFactory(factory).setCacheWriteDataSinkFactory(null).setFlags(CacheDataSource.FLAG_BLOCK_ON_CACHE);
    }

    private DemoUtil() {
    }
}
