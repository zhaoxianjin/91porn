package com.u91porn;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;

import com.bugsnag.android.Bugsnag;
import com.bugsnag.android.Severity;
import com.danikula.videocache.HttpProxyCacheServer;
import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.helper.loadviewhelper.load.LoadViewHelper;
import com.liulishuo.filedownloader.FileDownloader;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.squareup.leakcanary.LeakCanary;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.dao.DaoMaster;
import com.u91porn.data.dao.DaoSession;
import com.u91porn.data.dao.DataBaseManager;
import com.u91porn.data.dao.MySQLiteOpenHelper;
import com.u91porn.data.model.User;
import com.u91porn.eventbus.LowMemoryEvent;
import com.u91porn.utils.AddressHelper;
import com.u91porn.utils.AppCacheUtils;
import com.u91porn.utils.constants.Constants;
import com.u91porn.utils.constants.Keys;
import com.u91porn.utils.SPUtils;
import com.u91porn.utils.VideoCacheFileNameGenerator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.database.Database;

import java.io.File;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;
import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;

/**
 * 应用入口
 *
 * @author flymegoc
 * @date 2017/11/14
 */

public class MyApplication extends MultiDexApplication {

    private static final String TAG = MyApplication.class.getSimpleName();

    private static MyApplication mMyApplication;

    /**
     * 视频缓存
     */
    private HttpProxyCacheServer proxy;
    private CacheProviders cacheProviders;


    private User user;

    @Override
    public void onCreate() {
        super.onCreate();
        mMyApplication = this;
        initNightMode();
        AddressHelper.init(getApplicationContext());
        initLogger();
        initGreenDao3(this);
        initLeakCanry();
        initRxCache();
        initLoadingHelper();
        initFileDownload();
        if (!BuildConfig.DEBUG) {
            //初始化bug收集
            Bugsnag.init(this);
        }
        BGASwipeBackHelper.init(this, null);
    }

    private void initNightMode() {
        boolean isNightMode = (boolean) SPUtils.get(this, Keys.KEY_SP_OPEN_NIGHT_MODE, false);
        AppCompatDelegate.setDefaultNightMode(isNightMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void initFileDownload() {
        FileDownloader.setup(this);
    }

    /**
     * 初始化加载界面，空界面等
     */
    private void initLoadingHelper() {
        LoadViewHelper.getBuilder()
                .setLoadEmpty(R.layout.empty_view)
                .setLoadError(R.layout.error_view)
                .setLoadIng(R.layout.loading_view);
    }

    /**
     * 初始化greenDao3库
     */
    private void initGreenDao3(Context context) {
        //如果你想查看日志信息，请将DEBUG设置为true
        MigrationHelper.DEBUG = BuildConfig.DEBUG;
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context, Constants.DB_NAME, null);
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();
        DataBaseManager.init(daoSession);
    }

    public User getUser() {
        boolean isUserInfoNotComplete = user == null || user.getUserId() == 0 || TextUtils.isEmpty(user.getUserName());
        if (isUserInfoNotComplete) {
            return null;
        }
        if (user != null && (user.getUserId() == 0 || TextUtils.isEmpty(user.getUserName()))) {
            Bugsnag.notify(new Throwable("User info: " + user.toString()), Severity.WARNING);
        }
        return user;
    }

    public void setUser(User user) {
        if (user != null) {
            Logger.t(TAG).d(user.toString());
        }
        this.user = user;
    }

    /**
     * 获取视频缓存代理
     *
     * @return proxy
     */
    public HttpProxyCacheServer getProxy() {
        synchronized (MyApplication.class) {
            if (proxy == null) {
                proxy = newProxy();
            }
        }
        return proxy;
    }

    /**
     * 初始化视频缓存代理
     *
     * @return proxy
     */
    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                // 1 Gb for cache
                .maxCacheSize(AppCacheUtils.MAX_VIDEO_CACHE_SIZE)
                .cacheDirectory(AppCacheUtils.getVideoCacheDir(this))
                .fileNameGenerator(new VideoCacheFileNameGenerator())
                .build();
    }

    /**
     * 初始化内存分析工具
     */
    private void initLeakCanry() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...
    }

    /**
     * 初始化缓存
     */
    private void initRxCache() {
        File cacheDir = AppCacheUtils.getRxCacheDir(this);
        cacheProviders = new RxCache.Builder()
                .persistence(cacheDir, new GsonSpeaker())
                .using(CacheProviders.class);
    }

    public CacheProviders getCacheProviders() {
        return cacheProviders;
    }



    public static MyApplication getInstace() {
        return mMyApplication;
    }

    /**
     * 初始化日志工具
     */
    private void initLogger() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                // (Optional) Whether to show thread info or not. Default true
                .showThreadInfo(false)
                // (Optional) How many method line to show. Default 2
                .methodCount(0)
                // (Optional) Hides internal method calls up to offset. Default 5
                .methodOffset(5)
                // .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
                // .tag("My custom tag")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        boolean canReleaseMemory = (boolean) SPUtils.get(this, Keys.KEY_SP_FORBIDDEN_AUTO_RELEASE_MEMORY_WHEN_LOW_MEMORY, false);
        if (!canReleaseMemory) {
            EventBus.getDefault().post(new LowMemoryEvent(TAG));
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
