package com.u91porn.ui.pigav.playpigav;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u91porn.data.PigAvServiceApi;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.model.BaseResult;
import com.u91porn.data.model.PigAv;
import com.u91porn.data.model.PigAvVideo;
import com.u91porn.parser.ParsePigAv;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.rxjava.RxSchedulersHelper;
import com.u91porn.ui.MvpBasePresenter;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.Reply;

/**
 * @author flymegoc
 * @date 2018/1/30
 */

public class PlayPigAvPresenter extends MvpBasePresenter<PlayPigAvView> implements IPlayPigAv {
    private static final String TAG = PlayPigAvPresenter.class.getSimpleName();

    private PigAvServiceApi pigAvServiceApi;

    public PlayPigAvPresenter(CacheProviders cacheProviders, LifecycleProvider<Lifecycle.Event> provider, PigAvServiceApi pigAvServiceApi) {
        super(cacheProviders, provider);
        this.pigAvServiceApi = pigAvServiceApi;
    }

    @Override
    public void parseVideoUrl(String url, String pId, boolean pullToRefresh) {

        if (TextUtils.isEmpty(pId)) {
            pId = "aaa1";
            pullToRefresh = true;
        }
        DynamicKey dynamicKey = new DynamicKey(pId);
        cacheProviders.cacheWithNoLimitTime(pigAvServiceApi.video(url), dynamicKey, new EvictDynamicKey(pullToRefresh))
                .map(new Function<Reply<String>, String>() {
                    @Override
                    public String apply(Reply<String> stringReply) throws Exception {
                        return stringReply.getData();
                    }
                })
                .map(new Function<String, BaseResult<List<PigAv>>>() {
                    @Override
                    public BaseResult<List<PigAv>> apply(String s) throws Exception {
                        BaseResult<List<PigAv>> baseResult = ParsePigAv.parserVideoUrl(s);

                        return baseResult;
                    }
                })
                .compose(RxSchedulersHelper.<BaseResult<List<PigAv>>>ioMainThread())
                .compose(provider.<BaseResult<List<PigAv>>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<BaseResult<List<PigAv>>>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<PlayPigAvView>() {
                            @Override
                            public void run(@NonNull PlayPigAvView view) {
                                view.showLoading(true);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final BaseResult<List<PigAv>> baseResult) {
                        ifViewAttached(new ViewAction<PlayPigAvView>() {
                            @Override
                            public void run(@NonNull PlayPigAvView view) {
                                try {
                                    PigAvVideo pigAvVideo = new Gson().fromJson(baseResult.getMessage(), PigAvVideo.class);
                                    Logger.t(TAG).d(pigAvVideo.toString());
                                    view.showContent();
                                    view.playVideo(pigAvVideo);
                                    view.listVideo(baseResult.getData());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<PlayPigAvView>() {
                            @Override
                            public void run(@NonNull PlayPigAvView view) {
                                view.showError(msg);
                            }
                        });
                    }
                });
    }
}
