package com.u91porn.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.orhanobut.logger.Logger;
import com.u91porn.cookie.SetCookieCache;
import com.u91porn.cookie.SharedPrefsCookiePersistor;
import com.u91porn.di.ApplicationContext;
import com.u91porn.utils.AddressHelper;
import com.u91porn.utils.CommonHeaderInterceptor;
import com.u91porn.utils.RegexUtils;
import com.u91porn.utils.SPUtils;
import com.u91porn.utils.constants.Constants;
import com.u91porn.utils.constants.Keys;

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
    private PigAvServiceApi mPigAvServiceApi;
    private Mm99ServiceApi mMm99ServiceApi;
    private SharedPrefsCookiePersistor sharedPrefsCookiePersistor;
    private SetCookieCache setCookieCache;
    private PersistentCookieJar cookieJar;

    private Context context;

    /**
     * 需是applicationContext
     */
    public ApiManager(@ApplicationContext Context context) {
        this.context = context;
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
    public NoLimit91PornServiceApi init91PornRetrofitService(String baseUrl, boolean ignoreProxy) {
        Logger.t(TAG).d("begin init NoLimit91PornServiceApi...");
        sharedPrefsCookiePersistor = new SharedPrefsCookiePersistor(context);
        setCookieCache = new SetCookieCache();
        cookieJar = new PersistentCookieJar(setCookieCache, sharedPrefsCookiePersistor);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.cookieJar(cookieJar);
        //如果代理地址不为空，且端口正确设置Http代理
        boolean isOpenProxy = (boolean) SPUtils.get(context, Keys.KEY_SP_OPEN_HTTP_PROXY, false);
        String proxyHost = (String) SPUtils.get(context, Keys.KEY_SP_PROXY_IP_ADDRESS, "");
        int port = (int) SPUtils.get(context, Keys.KEY_SP_PROXY_PORT, 0);
        if (isOpenProxy && !ignoreProxy && RegexUtils.isIP(proxyHost) && port < Constants.PROXY_MAX_PORT && port > 0) {
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
        OkHttpClient okHttpClient;
        if (!ignoreProxy) {
            okHttpClient = RetrofitUrlManager.getInstance().with(builder)
                    .build();
        } else {
            okHttpClient = builder.build();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        return retrofit.create(NoLimit91PornServiceApi.class);
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

    public Forum91PronServiceApi initForum91RetrofitService(String baseUrl) {
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
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        return retrofit.create(Forum91PronServiceApi.class);
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

    public PigAvServiceApi initPigAvRetrofitService(String baseUrl) {
        Logger.t(TAG).d("begin init PigAvRetrofitService...");
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
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        return retrofit.create(PigAvServiceApi.class);
    }

    private void init99MmRetrofitService() {
        Logger.t(TAG).d("begin init init99MmRetrofitService...");
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
                .baseUrl(Api.APP_99_MM_DOMAIN)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        Logger.t(TAG).d("end init init99MmRetrofitService...");
        mMm99ServiceApi = retrofit.create(Mm99ServiceApi.class);
    }

    public NoLimit91PornServiceApi getNoLimit91PornService() {
        if (mNoLimit91PornServiceApi == null) {
            synchronized (NoLimit91PornServiceApi.class) {
                mNoLimit91PornServiceApi = init91PornRetrofitService(AddressHelper.getInstance().getVideo91PornAddress(), false);
                Logger.t(TAG).d("end init NoLimit91PornServiceApi...");
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
                mForum91PronServiceApi = initForum91RetrofitService(AddressHelper.getInstance().getForum91PornAddress());
                Logger.t(TAG).d("end init Forum91PronServiceApi...");
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

    public PigAvServiceApi getPigAvServiceApi() {
        if (mPigAvServiceApi == null) {
            synchronized (PigAvServiceApi.class) {
                mPigAvServiceApi = initPigAvRetrofitService(AddressHelper.getInstance().getPigAvAddress());
                Logger.t(TAG).d("end init PigAvRetrofitService...");
            }
        }
        return mPigAvServiceApi;
    }

    public Mm99ServiceApi getMm99ServiceApi() {
        if (mMm99ServiceApi == null) {
            synchronized (Mm99ServiceApi.class) {
                init99MmRetrofitService();
            }
        }
        return mMm99ServiceApi;
    }
}
