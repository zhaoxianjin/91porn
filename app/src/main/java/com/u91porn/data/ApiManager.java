package com.u91porn.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.orhanobut.logger.Logger;
import com.u91porn.cookie.SetCookieCache;
import com.u91porn.cookie.SharedPrefsCookiePersistor;
import com.u91porn.utils.AddressHelper;
import com.u91porn.utils.CommonHeaderInterceptor;
import com.u91porn.utils.Constants;
import com.u91porn.utils.Keys;
import com.u91porn.utils.RegexUtils;
import com.u91porn.utils.SPUtils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * @author flymegoc
 * @date 2018/1/27
 */

public class ApiManager {

    private static final String TAG = ApiManager.class.getSimpleName();

    private NoLimit91PornServiceApi mNoLimit91PornServiceApi;
    private GitHubServiceApi mGitHubServiceApi;
    private Forum91PronServiceApi mForum91PronServiceApi;
    private MeiZiTuServiceApi mMeiZiTuServiceApi;

    private SharedPrefsCookiePersistor sharedPrefsCookiePersistor;
    private SetCookieCache setCookieCache;
    private PersistentCookieJar cookieJar;

    private static ApiManager mApiManager;

    private ApiManager() {

    }

    public static ApiManager getInstance() {
        if (mApiManager == null) {
            synchronized (ApiManager.class) {
                mApiManager = new ApiManager();
            }
        }
        return mApiManager;
    }

    /**
     * 清除cookies
     */
    public void cleanCookies() {
        if (cookieJar == null) {
            return;
        }
        cookieJar.clear();
    }

    public SetCookieCache getSetCookieCache() {
        return setCookieCache;
    }

    public SharedPrefsCookiePersistor getSharedPrefsCookiePersistor() {
        return sharedPrefsCookiePersistor;
    }

    /**
     * 初始化Retrifit网络请求
     */
    public void init91PornRetrofitService(Context context) {
        Logger.t(TAG).d("begin init NoLimit91PornServiceApi...");
        sharedPrefsCookiePersistor = new SharedPrefsCookiePersistor(context);
        setCookieCache = new SetCookieCache();
        cookieJar = new PersistentCookieJar(setCookieCache, sharedPrefsCookiePersistor);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.cookieJar(cookieJar);
        //如果代理地址不为空，且端口正确设置Http代理
        boolean isOpenPxoxy = (boolean) SPUtils.get(context, Keys.KEY_SP_OPEN_HTTP_PROXY, false);
        String proxyHost = (String) SPUtils.get(context, Keys.KEY_SP_PROXY_IP_ADDRESS, "");
        int port = (int) SPUtils.get(context, Keys.KEY_SP_PROXY_PORT, 0);
        if (isOpenPxoxy && RegexUtils.isIP(proxyHost) && port < Constants.PROXY_MAX_PORT && port > 0) {
            Logger.t(TAG).d("代理设置： current91PornAddress:" + proxyHost + "  端口：" + port);
            builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, port)));
        }
        builder.addInterceptor(new CommonHeaderInterceptor());
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String message) {
                Logger.t(TAG).d("HttpLog:" + message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        builder.addInterceptor(logging);
        builder.readTimeout(5, TimeUnit.SECONDS);
        builder.writeTimeout(5, TimeUnit.SECONDS);
        builder.connectTimeout(5, TimeUnit.SECONDS);

        //动态切换baseUrl 配置
        OkHttpClient okHttpClient = RetrofitUrlManager.getInstance().with(builder)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(AddressHelper.getInstance().getVideo91PornAddress())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        mNoLimit91PornServiceApi = retrofit.create(NoLimit91PornServiceApi.class);
        Logger.t(TAG).d("end init NoLimit91PornServiceApi...");
    }

    private void initGitHubRetrofitService() {
        Logger.t(TAG).d("begin GitHubServiceApi...");
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new CommonHeaderInterceptor());
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(Api.APP_GITHUB_DOMAIN)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        mGitHubServiceApi = retrofit.create(GitHubServiceApi.class);
        Logger.t(TAG).d("end init GitHubServiceApi...");
    }

    public void initForum91RetrofitService() {
        Logger.t(TAG).d("begin init Forum91PronServiceApi...");
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new CommonHeaderInterceptor());
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String message) {
                Logger.t(TAG).d("HttpLog:" + message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        builder.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(AddressHelper.getInstance().getForum91PornAddress())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        mForum91PronServiceApi = retrofit.create(Forum91PronServiceApi.class);
        Logger.t(TAG).d("end init Forum91PronServiceApi...");
    }

    private void initMeiZiTuRetrofitService() {
        Logger.t(TAG).d("begin init MeiZiTuServiceApi...");
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new CommonHeaderInterceptor());
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String message) {
                Logger.t(TAG).d("HttpLog:" + message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        builder.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(Api.APP_MEIZITU_DOMAIN)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        mMeiZiTuServiceApi = retrofit.create(MeiZiTuServiceApi.class);
        Logger.t(TAG).d("end init MeiZiTuServiceApi...");
    }

    public NoLimit91PornServiceApi getNoLimit91PornService(Context context) {
        if (mNoLimit91PornServiceApi == null) {
            synchronized (NoLimit91PornServiceApi.class) {
                init91PornRetrofitService(context);
            }
        }
        return mNoLimit91PornServiceApi;
    }

    public MeiZiTuServiceApi getMeiZiTuServiceApi() {
        if (mMeiZiTuServiceApi == null) {
            synchronized (MeiZiTuServiceApi.class) {
                initMeiZiTuRetrofitService();
            }
        }
        return mMeiZiTuServiceApi;
    }

    public Forum91PronServiceApi getForum91PronServiceApi() {

        if (mForum91PronServiceApi == null) {
            synchronized (Forum91PronServiceApi.class) {
                initForum91RetrofitService();
            }
        }
        return mForum91PronServiceApi;
    }

    public GitHubServiceApi getGitHubServiceApi() {

        if (mGitHubServiceApi == null) {
            synchronized (GitHubServiceApi.class) {
                initGitHubRetrofitService();
            }
        }
        return mGitHubServiceApi;
    }
}
