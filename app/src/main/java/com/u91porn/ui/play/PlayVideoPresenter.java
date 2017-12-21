package com.u91porn.ui.play;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.orhanobut.logger.Logger;
import com.u91porn.MyApplication;
import com.u91porn.cookie.SetCookieCache;
import com.u91porn.cookie.SharedPrefsCookiePersistor;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.VideoResult;
import com.u91porn.ui.download.DownloadPresenter;
import com.u91porn.ui.favorite.FavoritePresenter;
import com.u91porn.utils.BoxQureyHelper;
import com.u91porn.utils.CallBackWrapper;
import com.u91porn.utils.ParseUtils;
import com.u91porn.utils.RandomIPAdderssUtils;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;

import io.objectbox.relation.RelationInfo;
import io.objectbox.relation.ToOne;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.Reply;
import okhttp3.Cookie;

/**
 * @author flymegoc
 * @date 2017/11/15
 * @describe
 */

public class PlayVideoPresenter extends MvpBasePresenter<PlayVideoView> implements IPlay {

    private static final String TAG = PlayVideoPresenter.class.getSimpleName();
    private NoLimit91PornServiceApi mNoLimit91PornServiceApi;
    private FavoritePresenter favoritePresenter;
    private DownloadPresenter downloadPresenter;
    private SharedPrefsCookiePersistor sharedPrefsCookiePersistor;
    private SetCookieCache setCookieCache;
    private CacheProviders cacheProviders;

    public PlayVideoPresenter(NoLimit91PornServiceApi mNoLimit91PornServiceApi, FavoritePresenter favoritePresenter, DownloadPresenter downloadPresenter, SharedPrefsCookiePersistor sharedPrefsCookiePersistor, SetCookieCache setCookieCache, CacheProviders cacheProviders) {
        this.mNoLimit91PornServiceApi = mNoLimit91PornServiceApi;
        this.favoritePresenter = favoritePresenter;
        this.downloadPresenter = downloadPresenter;
        this.sharedPrefsCookiePersistor = sharedPrefsCookiePersistor;
        this.setCookieCache = setCookieCache;
        this.cacheProviders = cacheProviders;
    }

    @Override
    public void loadVideoUrl(String viewKey) {

        String ip = RandomIPAdderssUtils.getRandomIPAdderss();
        cacheProviders.getVideoPlayPage(mNoLimit91PornServiceApi.getVideoPlayPage(viewKey, ip), new DynamicKey(viewKey), new EvictDynamicKey(false))
                .compose(getView().bindView())
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> responseBodyReply) throws Exception {
                        switch (responseBodyReply.getSource()) {
                            case CLOUD:
                                Logger.d("数据来自：网络");
                                break;
                            case MEMORY:
                                Logger.d("数据来自：内存");
                                break;
                            case PERSISTENCE:
                                Logger.d("数据来自：磁盘缓存");
                                break;
                            default:
                                break;
                        }
                        return responseBodyReply.getData();
                    }
                }).map(new Function<String, VideoResult>() {
            @Override
            public VideoResult apply(String s) throws Exception {
                return ParseUtils.parseVideoPlayUrl(s);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new CallBackWrapper<VideoResult>() {
            @Override
            public void onBegin(Disposable d) {
                if (isViewAttached()) {
                    getView().showParsingDialog();
                }
            }

            @Override
            public void onSuccess(VideoResult videoResult) {
                resetWatchTime();
                if (isViewAttached()) {
                    getView().playVideo(videoResult);
                }
            }

            @Override
            public void onError(String msg, int code) {
                if (isViewAttached()) {
                    getView().errorParseVideoUrl(msg + " code:" + code);
                }
            }
        });
    }

    /**
     * 检查并重置观看次数
     */
    private void resetWatchTime() {
        List<Cookie> cookieList = sharedPrefsCookiePersistor.loadAll();
        for (Cookie cookie : cookieList) {
            if ("watch_times".equals(cookie.name())) {
                if ("10".equals(cookie.value())) {
                    Logger.t(TAG).d("已经观看10次，重置cookies");
                    sharedPrefsCookiePersistor.delete(cookie);
                    setCookieCache.delete(cookie);
                } else {
                    Logger.t(TAG).d("当前已经看了：" + cookie.value() + " 次");
                }
            }
        }

    }

    @Override
    public void saveVideoUrl(VideoResult videoResult, UnLimit91PornItem unLimit91PornItem) {
        Box<UnLimit91PornItem> unLimit91PornItemBox = MyApplication.getInstace().getBoxStore().boxFor(UnLimit91PornItem.class);
        UnLimit91PornItem tmp = BoxQureyHelper.findByViewKey(unLimit91PornItem.getViewKey());
        if (tmp == null) {
            unLimit91PornItem.setFavorite(UnLimit91PornItem.FAVORITE_NO);
            unLimit91PornItem.videoResult.setTarget(videoResult);
            unLimit91PornItemBox.put(unLimit91PornItem);
        } else {
            videoResult.setId(tmp.getId());
            tmp.videoResult.setTarget(videoResult);
            unLimit91PornItemBox.put(tmp);
        }
    }

    @Override
    public void downloadVideo(UnLimit91PornItem unLimit91PornItem) {
        downloadPresenter.downloadVideo(unLimit91PornItem, new DownloadPresenter.DownloadListener() {
            @Override
            public void onSuccess(String message) {
                if (isViewAttached()) {
                    getView().showMessage(message);
                }
            }

            @Override
            public void onError(String message) {
                if (isViewAttached()) {
                    getView().showMessage(message);
                }
            }
        });
    }

    @Override
    public void favorite(String cpaintFunction, String uId, String videoId, String ownnerId, String responseType) {
        favoritePresenter.favorite(cpaintFunction, uId, videoId, ownnerId, responseType, new FavoritePresenter.FavoriteListener() {
            @Override
            public void onSuccess(String message) {
                if (isViewAttached()) {
                    getView().showMessage(message);
                }
            }

            @Override
            public void onError(String message) {
                if (isViewAttached()) {
                    getView().showError(new Throwable(message), false);
                }
            }
        });
    }
}
