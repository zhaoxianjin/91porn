package com.u91porn;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;

import com.bugsnag.android.Bugsnag;
import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.helper.loadviewhelper.load.LoadViewHelper;
import com.liulishuo.filedownloader.FileDownloader;
import com.squareup.leakcanary.LeakCanary;
import com.u91porn.data.dao.DaoMaster;
import com.u91porn.data.dao.DaoSession;
import com.u91porn.data.dao.DataBaseManager;
import com.u91porn.data.dao.MySQLiteOpenHelper;
import com.u91porn.di.component.ApplicationComponent;
import com.u91porn.di.component.DaggerApplicationComponent;
import com.u91porn.di.module.ApplicationModule;
import com.u91porn.eventbus.LowMemoryEvent;
import com.u91porn.utils.AddressHelper;
import com.u91porn.utils.AppLogger;
import com.u91porn.utils.SPUtils;
import com.u91porn.utils.constants.Constants;
import com.u91porn.utils.constants.Keys;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.database.Database;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;

/**
 * 应用入口
 *
 * @author flymegoc
 * @date 2017/11/14
 */

public class MyApplication extends MultiDexApplication {

    private static final String TAG = MyApplication.class.getSimpleName();

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();
        applicationComponent.inject(this);
        initNightMode();
        AddressHelper.init(getApplicationContext());
        AppLogger.initLogger();
        initGreenDao3(this);
        initLeakCanry();
        initLoadingHelper();
        initFileDownload();
        if (!BuildConfig.DEBUG) {
            //初始化bug收集
            Bugsnag.init(this);
        }
        BGASwipeBackHelper.init(this, null);
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
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

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        boolean canReleaseMemory = (boolean) SPUtils.get(this, Keys.KEY_SP_FORBIDDEN_AUTO_RELEASE_MEMORY_WHEN_LOW_MEMORY, false);
        if (!canReleaseMemory) {
            EventBus.getDefault().post(new LowMemoryEvent(TAG));
        }
    }

}
